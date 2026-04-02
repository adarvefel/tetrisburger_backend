package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.application.event.ProductImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.enums.ProductType;
import com.tetris.tetrisburger_backend.domain.exception.ProductAlreadyExistsException;
import com.tetris.tetrisburger_backend.domain.exception.ProductCategoryNotFoundException;
import com.tetris.tetrisburger_backend.domain.exception.SupplierNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.domain.port.in.product.command.CreateProductCommand;
import com.tetris.tetrisburger_backend.domain.port.out.ProductCategoryRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import com.tetris.tetrisburger_backend.domain.port.out.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de CreateProductUseCase")
class CreateProductUseCaseTest {

    @Mock private ProductRepository         productRepository;
    @Mock private SupplierRepository        supplierRepository;
    @Mock private ProductCategoryRepository productCategoryRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CreateProductUseCase useCase;

    @Captor private ArgumentCaptor<Product>                          productCaptor;
    @Captor private ArgumentCaptor<ProductImageUploadRequestedEvent> eventCaptor;

    // -- constantes --
    private static final String     NAME        = "Hamburguesa Clasica";
    private static final Integer    CATEGORY_ID = 1;
    private static final Integer    SUPPLIER_ID = 2;
    private static final Integer    CREATED_BY  = 99;
    private static final BigDecimal PRICE       = BigDecimal.valueOf(15000);
    private static final ProductType PRODUCT_TYPE = ProductType.SIDE;

    private Supplier        supplier;
    private ProductCategory category;
    private Product         savedProduct;
    private FileData        validFileData;

    @BeforeEach
    void setUp() {
        supplier     = mock(Supplier.class);
        category     = mock(ProductCategory.class);
        savedProduct = mock(Product.class);

        when(savedProduct.getId()).thenReturn(10);

        validFileData = mock(FileData.class);
        lenient().when(validFileData.bytes()).thenReturn(new byte[]{1, 2, 3});
        lenient().when(validFileData.contentType()).thenReturn("image/jpeg");
        lenient().when(validFileData.originalFilename()).thenReturn("burger.jpg");
    }

    // -----------------------------------------------------------------
    // Creacion exitosa sin imagen
    // -----------------------------------------------------------------
    @Nested
    @DisplayName("Creacion exitosa sin imagen")
    class SinImagenTests {

        @BeforeEach
        void givenHappyPath() {
            when(productRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(NAME)).thenReturn(false);
            when(productCategoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
            when(supplierRepository.findById(SUPPLIER_ID)).thenReturn(Optional.of(supplier));
            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        }

        @Test
        @DisplayName("debe retornar el producto guardado")
        void shouldReturnSavedProduct() {
            Product result = useCase.create(buildCommand(NAME, CATEGORY_ID, SUPPLIER_ID, null));

            assertThat(result).isNotNull().isSameAs(savedProduct);
        }

        @Test
        @DisplayName("debe guardar el producto con imageUrl e imageKey nulos")
        void shouldSaveProductWithNullImageFields() {
            useCase.create(buildCommand(NAME, CATEGORY_ID, SUPPLIER_ID, null));

            verify(productRepository).save(productCaptor.capture());
            assertThat(productCaptor.getValue().getImageUrl()).isNull();
            assertThat(productCaptor.getValue().getImageKey()).isNull();
        }

        @Test
        @DisplayName("no debe publicar evento cuando no hay imagen")
        void shouldNotPublishEventWhenNoImage() {
            useCase.create(buildCommand(NAME, CATEGORY_ID, SUPPLIER_ID, null));

            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("debe buscar la categoria con el ID del comando")
        void shouldFindCategoryWithCorrectId() {
            useCase.create(buildCommand(NAME, CATEGORY_ID, SUPPLIER_ID, null));

            verify(productCategoryRepository).findById(CATEGORY_ID);
        }

        @Test
        @DisplayName("debe buscar el proveedor con el ID del comando")
        void shouldFindSupplierWithCorrectId() {
            useCase.create(buildCommand(NAME, CATEGORY_ID, SUPPLIER_ID, null));

            verify(supplierRepository).findById(SUPPLIER_ID);
        }
    }

    // -----------------------------------------------------------------
    // Creacion exitosa con imagen
    // -----------------------------------------------------------------
    @Nested
    @DisplayName("Creacion exitosa con imagen")
    class ConImagenTests {

        @BeforeEach
        void givenHappyPath() {
            when(productRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(NAME)).thenReturn(false);
            when(productCategoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
            when(supplierRepository.findById(SUPPLIER_ID)).thenReturn(Optional.of(supplier));
            when(productRepository.save(any())).thenReturn(savedProduct);
        }

        @Test
        @DisplayName("debe publicar ProductImageUploadRequestedEvent con datos correctos")
        void shouldPublishEventWithCorrectData() {
            useCase.create(buildCommand(NAME, CATEGORY_ID, SUPPLIER_ID, validFileData));

            verify(eventPublisher).publishEvent(eventCaptor.capture());

            ProductImageUploadRequestedEvent event = eventCaptor.getValue();
            assertThat(event.productId()).isEqualTo(10);
            assertThat(event.imageBytes()).isEqualTo(new byte[]{1, 2, 3});
            assertThat(event.contentType()).isEqualTo("image/jpeg");
            assertThat(event.originalFilename()).isEqualTo("burger.jpg");
            assertThat(event.updatedBy()).isEqualTo(CREATED_BY);

        }

        @Test
        @DisplayName("debe publicar el evento con el ID del producto ya guardado")
        void shouldPublishEventWithSavedProductId() {
            when(savedProduct.getId()).thenReturn(77);

            useCase.create(buildCommand(NAME, CATEGORY_ID, SUPPLIER_ID, validFileData));

            verify(eventPublisher).publishEvent(eventCaptor.capture());
            assertThat(eventCaptor.getValue().productId()).isEqualTo(77);
        }

        @Test
        @DisplayName("debe guardar antes de publicar el evento")
        void shouldSaveBeforePublishingEvent() {
            useCase.create(buildCommand(NAME, CATEGORY_ID, SUPPLIER_ID, validFileData));

            var order = inOrder(productRepository, eventPublisher);
            order.verify(productRepository).save(any());
            order.verify(eventPublisher).publishEvent(any());
        }
    }

    // -----------------------------------------------------------------
    // Trim del nombre
    // -----------------------------------------------------------------
    @Nested
    @DisplayName("Normalizacion del nombre (trim)")
    class TrimTests {

        @ParameterizedTest(name = "nombre=''{0}''")
        @ValueSource(strings = {
                "  Hamburguesa Clasica  ",
                "Hamburguesa Clasica   ",
                "   Hamburguesa Clasica"
        })
        @DisplayName("debe recortar espacios antes de validar duplicado")
        void shouldTrimNameBeforeDuplicateCheck(String nameWithSpaces) {
            when(productRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(NAME)).thenReturn(false);
            when(supplierRepository.findById(SUPPLIER_ID)).thenReturn(Optional.of(supplier));
            when(productRepository.save(any())).thenReturn(savedProduct);

            useCase.create(buildCommand(nameWithSpaces, null, SUPPLIER_ID, null));

            verify(productRepository).existsByNameIgnoreCaseAndDeletedAtIsNull(NAME);
        }

        @Test
        @DisplayName("debe guardar el producto con el nombre sin espacios")
        void shouldSaveProductWithTrimmedName() {
            when(productRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(NAME)).thenReturn(false);
            when(supplierRepository.findById(SUPPLIER_ID)).thenReturn(Optional.of(supplier));
            when(productRepository.save(any())).thenReturn(savedProduct);

            useCase.create(buildCommand("  " + NAME + "  ", null, SUPPLIER_ID, null));

            verify(productRepository).save(productCaptor.capture());
            assertThat(productCaptor.getValue().getName()).isEqualTo(NAME);
        }
    }

    // -----------------------------------------------------------------
    // Nombre duplicado
    // -----------------------------------------------------------------
    @Nested
    @DisplayName("Nombre duplicado")
    class NombreDuplicadoTests {

        @Test
        @DisplayName("debe lanzar ProductAlreadyExistsException cuando el nombre ya existe")
        void shouldThrowWhenNameAlreadyExists() {
            when(productRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(NAME)).thenReturn(true);

            assertThatThrownBy(() -> useCase.create(buildCommand(NAME, CATEGORY_ID, SUPPLIER_ID, null)))
                    .isInstanceOf(ProductAlreadyExistsException.class);
        }

        @Test
        @DisplayName("no debe consultar dependencias ni guardar cuando el nombre existe")
        void shouldNotCallDependenciesWhenNameExists() {
            when(productRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(NAME)).thenReturn(true);

            assertThatThrownBy(() -> useCase.create(buildCommand(NAME, CATEGORY_ID, SUPPLIER_ID, null)))
                    .isInstanceOf(ProductAlreadyExistsException.class);

            verifyNoInteractions(productCategoryRepository, supplierRepository);
            verify(productRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }
    }

    // -----------------------------------------------------------------
    // Categoria opcional
    // -----------------------------------------------------------------
    @Nested
    @DisplayName("Validacion de categoria")
    class CategoriaTests {

        @Test
        @DisplayName("debe crear el producto sin categoria cuando productCategoryId es null")
        void shouldCreateWithNullCategoryWhenIdIsNull() {
            when(productRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(NAME)).thenReturn(false);
            when(supplierRepository.findById(SUPPLIER_ID)).thenReturn(Optional.of(supplier));
            when(productRepository.save(any())).thenReturn(savedProduct);

            Product result = useCase.create(buildCommand(NAME, null, SUPPLIER_ID, null));

            assertThat(result).isSameAs(savedProduct);
            verifyNoInteractions(productCategoryRepository);
            verify(productRepository).save(productCaptor.capture());
            assertThat(productCaptor.getValue().getCategoryName()).isNull();
        }

        @Test
        @DisplayName("debe lanzar ProductCategoryNotFoundException cuando la categoria no existe")
        void shouldThrowWhenCategoryNotFound() {
            when(productRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(NAME)).thenReturn(false);
            when(productCategoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.create(buildCommand(NAME, CATEGORY_ID, SUPPLIER_ID, null)))
                    .isInstanceOf(ProductCategoryNotFoundException.class)
                    .hasMessageContaining(CATEGORY_ID.toString());
        }

        @Test
        @DisplayName("no debe buscar proveedor ni guardar cuando la categoria no existe")
        void shouldNotProceedWhenCategoryNotFound() {
            when(productRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(NAME)).thenReturn(false);
            when(productCategoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.create(buildCommand(NAME, CATEGORY_ID, SUPPLIER_ID, null)))
                    .isInstanceOf(ProductCategoryNotFoundException.class);

            verifyNoInteractions(supplierRepository);
            verify(productRepository, never()).save(any());
        }
    }

    // -----------------------------------------------------------------
    // Validacion de proveedor
    // -----------------------------------------------------------------
    @Nested
    @DisplayName("Validacion de proveedor")
    class ProveedorTests {

        @BeforeEach
        void givenNoNameDuplicate() {
            when(productRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(NAME)).thenReturn(false);
        }

        @Test
        @DisplayName("debe lanzar SupplierNotFoundException cuando supplierId es null")
        void shouldThrowWhenSupplierIdIsNull() {
            assertThatThrownBy(() -> useCase.create(buildCommand(NAME, null, null, null)))
                    .isInstanceOf(SupplierNotFoundException.class)
                    .hasMessageContaining("requerido");
        }

        @Test
        @DisplayName("debe lanzar SupplierNotFoundException cuando el proveedor no existe en BD")
        void shouldThrowWhenSupplierNotFoundInDb() {
            when(supplierRepository.findById(SUPPLIER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.create(buildCommand(NAME, null, SUPPLIER_ID, null)))
                    .isInstanceOf(SupplierNotFoundException.class)
                    .hasMessageContaining(SUPPLIER_ID.toString());
        }

        @Test
        @DisplayName("no debe guardar el producto cuando el proveedor no existe")
        void shouldNotSaveWhenSupplierNotFound() {
            when(supplierRepository.findById(SUPPLIER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.create(buildCommand(NAME, null, SUPPLIER_ID, null)))
                    .isInstanceOf(SupplierNotFoundException.class);

            verify(productRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("no debe consultar repositorio cuando supplierId es null")
        void shouldNotQueryRepositoryWhenSupplierIdIsNull() {
            assertThatThrownBy(() -> useCase.create(buildCommand(NAME, null, null, null)))
                    .isInstanceOf(SupplierNotFoundException.class);

            verifyNoInteractions(supplierRepository);
        }
    }

    // -----------------------------------------------------------------
    // Orden de llamadas
    // -----------------------------------------------------------------
    @Nested
    @DisplayName("Orden de llamadas")
    class FlowOrderTests {

        @Test
        @DisplayName("debe seguir: existsByName -> findCategory -> findSupplier -> save -> publishEvent")
        void shouldFollowCorrectOrder() {
            when(productRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(NAME)).thenReturn(false);
            when(productCategoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
            when(supplierRepository.findById(SUPPLIER_ID)).thenReturn(Optional.of(supplier));
            when(productRepository.save(any())).thenReturn(savedProduct);

            useCase.create(buildCommand(NAME, CATEGORY_ID, SUPPLIER_ID, validFileData));

            var order = inOrder(productRepository, productCategoryRepository, supplierRepository, eventPublisher);
            order.verify(productRepository).existsByNameIgnoreCaseAndDeletedAtIsNull(NAME);
            order.verify(productCategoryRepository).findById(CATEGORY_ID);
            order.verify(supplierRepository).findById(SUPPLIER_ID);
            order.verify(productRepository).save(any());
            order.verify(eventPublisher).publishEvent(any());
        }
    }

    // -----------------------------------------------------------------
    // Helper — orden exacto del record CreateProductCommand
    // -----------------------------------------------------------------
    private CreateProductCommand buildCommand(
            String name,
            Integer categoryId,
            Integer supplierId,
            FileData fileData
    ) {
        return new CreateProductCommand(
                name,                   // name
                "Descripcion",          // description
                50,                     // quantity
                PRICE,                  // price
                true,                   // availability
                PRODUCT_TYPE,           // productType  <-- ProductType enum
                false,                  // isBurgerIngredient
                fileData,               // productImageData
                categoryId,             // productCategoryId
                supplierId,             // supplierId
                CREATED_BY              // createdBy
        );
    }
}
