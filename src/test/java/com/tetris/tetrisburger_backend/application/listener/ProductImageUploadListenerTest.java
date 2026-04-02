package com.tetris.tetrisburger_backend.application.listener;

import com.tetris.tetrisburger_backend.application.event.ProductImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;
import com.tetris.tetrisburger_backend.domain.exception.ImageUploadException;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de ProductImageUploadListener")
class ProductImageUploadListenerTest {

    @Mock private ProductRepository productRepository;
    @Mock private ImageStoragePort  imageStoragePort;

    @InjectMocks
    private ProductImageUploadListener listener;

    @Captor private ArgumentCaptor<FileData> fileDataCaptor;
    @Captor private ArgumentCaptor<Product>  productCaptor;

    // -- constantes alineadas con el record --
    private static final Integer PRODUCT_ID   = 10;           // Integer, no Long
    private static final Integer UPDATED_BY   = 1;
    private static final String  FILENAME     = "burger.jpg";
    private static final String  CONTENT_TYPE = "image/jpeg";
    private static final byte[]  IMAGE_BYTES  = {1, 2, 3};
    private static final String  IMAGE_KEY    = "products/10/burger.jpg";
    private static final String  IMAGE_URL    = "https://cdn.tetris.com/products/10/burger.jpg";

    private ProductImageUploadRequestedEvent event;
    private Product                          product;
    private ImageUploadResult                uploadResult;

    @BeforeEach
    void setUp() {
        // Orden correcto: productId, imageBytes, contentType, originalFilename, updatedBy
        event = new ProductImageUploadRequestedEvent(
                PRODUCT_ID,
                IMAGE_BYTES,
                CONTENT_TYPE,
                FILENAME,
                UPDATED_BY
        );

        product      = mock(Product.class);
        uploadResult = new ImageUploadResult(IMAGE_KEY,FILENAME );
    }

    // -----------------------------------------------------------------
    // Happy path
    // -----------------------------------------------------------------
    @Nested
    @DisplayName("Upload exitoso")
    class UploadExitosoTests {

        @BeforeEach
        void givenSetup() {
            when(imageStoragePort.uploadProductImage(any(FileData.class))).thenReturn(uploadResult);
            when(imageStoragePort.getImageUrl(IMAGE_KEY)).thenReturn(IMAGE_URL);
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        }

        @Test
        @DisplayName("debe subir la imagen, actualizar el producto y guardarlo")
        void shouldUploadUpdateAndSave() {
            listener.handleProductImageUpload(event);

            verify(imageStoragePort).uploadProductImage(any(FileData.class));
            verify(product).updateImage(IMAGE_KEY, IMAGE_URL, UPDATED_BY);
            verify(productRepository).save(product);
        }

        @Test
        @DisplayName("debe construir FileData con los datos exactos del evento")
        void shouldBuildFileDataWithEventData() {
            listener.handleProductImageUpload(event);

            verify(imageStoragePort).uploadProductImage(fileDataCaptor.capture());

            FileData captured = fileDataCaptor.getValue();
            assertThat(captured.originalFilename()).isEqualTo(FILENAME);
            assertThat(captured.contentType()).isEqualTo(CONTENT_TYPE);
            assertThat(captured.bytes()).isEqualTo(IMAGE_BYTES);
        }

        @Test
        @DisplayName("debe llamar updateImage con imageKey e imageUrl correctos")
        void shouldCallUpdateImageWithCorrectKeys() {
            listener.handleProductImageUpload(event);

            verify(product).updateImage(IMAGE_KEY, IMAGE_URL, UPDATED_BY);
        }
        @Test
        @DisplayName("debe resolver imageUrl desde la imageKey retornada por el storage")
        void shouldResolveImageUrlFromReturnedKey() {
            String otherKey = "products/10/other.jpg";
            String otherUrl = "https://cdn.tetris.com/products/10/other.jpg";

            when(imageStoragePort.uploadProductImage(any()))
                    .thenReturn(new ImageUploadResult(otherKey, "other.jpg"));  // fix
            when(imageStoragePort.getImageUrl(otherKey)).thenReturn(otherUrl);

            listener.handleProductImageUpload(event);

            verify(imageStoragePort).getImageUrl(otherKey);
            verify(product).updateImage(otherKey, otherUrl, UPDATED_BY);
        }


        @Test
        @DisplayName("debe guardar el mismo producto recuperado del repositorio")
        void shouldSaveTheProductFromRepository() {
            listener.handleProductImageUpload(event);

            verify(productRepository).save(productCaptor.capture());
            assertThat(productCaptor.getValue()).isSameAs(product);
        }

        @Test
        @DisplayName("debe seguir el orden: upload -> getUrl -> findById -> updateImage -> save")
        void shouldFollowCorrectCallOrder() {
            listener.handleProductImageUpload(event);

            var order = inOrder(imageStoragePort, productRepository, product);
            order.verify(imageStoragePort).uploadProductImage(any());
            order.verify(imageStoragePort).getImageUrl(IMAGE_KEY);
            order.verify(productRepository).findById(PRODUCT_ID);
            order.verify(product).updateImage(any(), any(), any());
            order.verify(productRepository).save(product);
        }
    }

    // -----------------------------------------------------------------
    // Producto no encontrado
    // -----------------------------------------------------------------
    @Nested
    @DisplayName("Producto no encontrado")
    class ProductoNoEncontradoTests {

        @BeforeEach
        void givenStorageOkButProductMissing() {
            when(imageStoragePort.uploadProductImage(any())).thenReturn(uploadResult);
            when(imageStoragePort.getImageUrl(any())).thenReturn(IMAGE_URL);
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());
        }

        @Test
        @DisplayName("debe lanzar ImageUploadException cuando el producto no existe")
        void shouldThrowImageUploadExceptionWhenProductNotFound() {
            assertThatThrownBy(() -> listener.handleProductImageUpload(event))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessageContaining("Error al subir imagen del producto");
        }

        @Test
        @DisplayName("la causa debe ser IllegalArgumentException con el ID del producto")
        void shouldHaveIllegalArgumentExceptionCause() {
            assertThatThrownBy(() -> listener.handleProductImageUpload(event))
                    .isInstanceOf(ImageUploadException.class)
                    .cause()
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(PRODUCT_ID.toString());
        }

        @Test
        @DisplayName("no debe llamar a updateImage ni save cuando el producto no existe")
        void shouldNotUpdateOrSaveWhenProductNotFound() {
            assertThatThrownBy(() -> listener.handleProductImageUpload(event))
                    .isInstanceOf(ImageUploadException.class);

            verify(product, never()).updateImage(any(), any(), any());
            verify(productRepository, never()).save(any());
        }
    }

    // -----------------------------------------------------------------
    // Errores del storage
    // -----------------------------------------------------------------
    @Nested
    @DisplayName("Errores del ImageStoragePort")
    class ErroresStorageTests {

        @Test
        @DisplayName("debe lanzar ImageUploadException cuando uploadProductImage falla")
        void shouldThrowWhenUploadFails() {
            when(imageStoragePort.uploadProductImage(any()))
                    .thenThrow(new RuntimeException("S3 no disponible"));

            assertThatThrownBy(() -> listener.handleProductImageUpload(event))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessageContaining("Error al subir imagen del producto")
                    .cause()
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("S3 no disponible");
        }

        @Test
        @DisplayName("debe lanzar ImageUploadException cuando getImageUrl falla")
        void shouldThrowWhenGetImageUrlFails() {
            when(imageStoragePort.uploadProductImage(any())).thenReturn(uploadResult);
            when(imageStoragePort.getImageUrl(IMAGE_KEY))
                    .thenThrow(new RuntimeException("No se pudo generar URL"));

            assertThatThrownBy(() -> listener.handleProductImageUpload(event))
                    .isInstanceOf(ImageUploadException.class)
                    .cause()
                    .hasMessage("No se pudo generar URL");
        }

        @Test
        @DisplayName("no debe guardar el producto cuando el upload falla")
        void shouldNotSaveWhenUploadFails() {
            when(imageStoragePort.uploadProductImage(any()))
                    .thenThrow(new RuntimeException("Timeout"));

            assertThatThrownBy(() -> listener.handleProductImageUpload(event))
                    .isInstanceOf(ImageUploadException.class);

            verify(productRepository, never()).save(any());
        }
    }

    // -----------------------------------------------------------------
    // Envoltura de excepciones
    // -----------------------------------------------------------------
    @Nested
    @DisplayName("Envoltura de excepciones")
    class EnvolturaExcepcionesTests {

        @Test
        @DisplayName("debe envolver cualquier excepcion inesperada en ImageUploadException")
        void shouldWrapAnyUnexpectedException() {
            when(imageStoragePort.uploadProductImage(any()))
                    .thenThrow(new NullPointerException("NPE inesperado"));

            assertThatThrownBy(() -> listener.handleProductImageUpload(event))
                    .isInstanceOf(ImageUploadException.class)
                    .cause()
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("nunca debe propagar la excepcion original sin envolver")
        void shouldNeverPropagateRawException() {
            when(imageStoragePort.uploadProductImage(any()))
                    .thenThrow(new IllegalStateException("Estado invalido"));

            assertThatThrownBy(() -> listener.handleProductImageUpload(event))
                    .isNotInstanceOf(IllegalStateException.class)
                    .isInstanceOf(ImageUploadException.class);
        }
    }
}
