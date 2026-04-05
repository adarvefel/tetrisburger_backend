package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.enums.PaymentMethod;
import com.tetris.tetrisburger_backend.domain.exception.UnauthorizedException;
import com.tetris.tetrisburger_backend.domain.model.AdditionSettings;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.Cart;
import com.tetris.tetrisburger_backend.domain.model.CartItem;
import com.tetris.tetrisburger_backend.domain.model.FavoriteBurger;
import com.tetris.tetrisburger_backend.domain.model.FavoriteBurgerDetail;
import com.tetris.tetrisburger_backend.domain.model.Invoice;
import com.tetris.tetrisburger_backend.domain.model.MenuCategory;
import com.tetris.tetrisburger_backend.domain.model.Payment;
import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.domain.model.WhatsappSettings;
import com.tetris.tetrisburger_backend.domain.port.in.additionsettings.GetAdditionSettings;
import com.tetris.tetrisburger_backend.domain.port.in.additionsettings.UpdateAdditionSettings;
import com.tetris.tetrisburger_backend.domain.port.in.cart.AddCart;
import com.tetris.tetrisburger_backend.domain.port.in.cart.ClearCart;
import com.tetris.tetrisburger_backend.domain.port.in.cart.GetCart;
import com.tetris.tetrisburger_backend.domain.port.in.favoriteburger.AddFavoriteBurger;
import com.tetris.tetrisburger_backend.domain.port.in.favoriteburger.GetFavoriteBurgersByUser;
import com.tetris.tetrisburger_backend.domain.port.in.favoriteburger.RemoveFavoriteBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.GetBurgerById;
import com.tetris.tetrisburger_backend.domain.port.in.invoice.GetInvoiceByOrder;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.CreateMenuCategory;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.DeleteMenuCategory;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.GetMenuCategoryById;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.ListMenuCategory;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.UpdateMenuCategory;
import com.tetris.tetrisburger_backend.domain.port.in.payment.CreatePayment;
import com.tetris.tetrisburger_backend.domain.port.in.payment.command.CreatePaymentCommand;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.CreateProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.DeleteProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.GetProductCategoryById;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.ListProductCategories;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.ListPublicProductCategories;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.UpdateProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.CreateSupplier;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.DeleteSupplier;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.GetSupplierById;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.ListSuppliers;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.UpdateSupplier;
import com.tetris.tetrisburger_backend.domain.port.in.whatsapp.GetWhatsappSettings;
import com.tetris.tetrisburger_backend.domain.port.in.whatsapp.UpdateWhatsappSettings;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.DeleteResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.additionsettings.AdditionSettingsResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.additionsettings.UpdateAdditionSettingsRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.cart.CartItemRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.cart.CartItemResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.favoriteburger.FavoriteBurgerRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.favoriteburger.FavoriteBurgerResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.favoriteburger.FavoriteBurgerSimpleResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menucategory.CreateMenuCategoryRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menucategory.MenuCategoryResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menucategory.UpdateMenuCategoryRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.payment.CreatePaymentRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.payment.PaymentResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory.ListProductCategoryResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory.ProductCategoryResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.supplier.ListSupplierResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.supplier.SupplierResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.whatsapp.UpdateWhatsappSettingsRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.whatsapp.WhatsappSettingsResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.AdditionSettingsDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.CartRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.FavoriteBurgerRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.InvoiceRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.MenuCategoryDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.PaymentRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.ProductCategoryRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.SupplierRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.WhatsappSettingsRestMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Controllers - pruebas unitarias directas")
class RestControllersUnitTest {

    @Nested
    class AuthenticatedControllerTests {

        @Test
        void shouldExtractUserIdFromCustomUserDetails() {
            TestAuthenticatedController controller = new TestAuthenticatedController();
            CustomUserDetails userDetails = mock(CustomUserDetails.class);

            when(userDetails.getId()).thenReturn(77);

            Integer userId = controller.extract(userDetails);

            assertThat(userId).isEqualTo(77);
        }

        @Test
        void shouldThrowWhenUserDetailsIsNotCustomUserDetails() {
            TestAuthenticatedController controller = new TestAuthenticatedController();
            UserDetails userDetails = mock(UserDetails.class);

            assertThatThrownBy(() -> controller.extract(userDetails))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage("Usuario no autenticado correctamente");
        }
    }

    @Nested
    class AdditionSettingsControllerTests {

        @Test
        void shouldReturnCurrentSettings() {
            GetAdditionSettings getAdditionSettings = mock(GetAdditionSettings.class);
            UpdateAdditionSettings updateAdditionSettings = mock(UpdateAdditionSettings.class);
            AdditionSettingsDtoMapper mapper = mock(AdditionSettingsDtoMapper.class);
            AdditionSettingsController controller = new AdditionSettingsController(getAdditionSettings, updateAdditionSettings, mapper);
            AdditionSettings settings = mock(AdditionSettings.class);
            AdditionSettingsResponseDTO responseDTO = new AdditionSettingsResponseDTO(1, 3, new BigDecimal("15000"), true, LocalDateTime.now());

            when(getAdditionSettings.handle()).thenReturn(settings);
            when(mapper.toResponseDTO(settings)).thenReturn(responseDTO);

            ResponseEntity<AdditionSettingsResponseDTO> response = controller.get();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isSameAs(responseDTO);
        }

        @Test
        void shouldUpdateSettings() {
            GetAdditionSettings getAdditionSettings = mock(GetAdditionSettings.class);
            UpdateAdditionSettings updateAdditionSettings = mock(UpdateAdditionSettings.class);
            AdditionSettingsDtoMapper mapper = mock(AdditionSettingsDtoMapper.class);
            AdditionSettingsController controller = new AdditionSettingsController(getAdditionSettings, updateAdditionSettings, mapper);
            AdditionSettings settings = mock(AdditionSettings.class);
            AdditionSettingsResponseDTO responseDTO = new AdditionSettingsResponseDTO(1, 4, new BigDecimal("20000"), true, LocalDateTime.now());

            when(mapper.toCommand(null)).thenReturn(null);
            when(updateAdditionSettings.handle(null)).thenReturn(settings);
            when(mapper.toResponseDTO(settings)).thenReturn(responseDTO);

            ResponseEntity<AdditionSettingsResponseDTO> response = controller.update(null);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isSameAs(responseDTO);
            verify(updateAdditionSettings).handle(null);
        }
    }

    @Nested
    class PaymentControllerTests {

        @Test
        void shouldCreatePaymentUsingAuthenticatedUser() {
            CreatePayment createPayment = mock(CreatePayment.class);
            PaymentRestDtoMapper mapper = mock(PaymentRestDtoMapper.class);
            PaymentController controller = new PaymentController(createPayment, mapper);
            CreatePaymentRequestDTO requestDTO = new CreatePaymentRequestDTO(15, PaymentMethod.CARD);
            CustomUserDetails userDetails = mock(CustomUserDetails.class);
            Payment payment = mock(Payment.class);
            PaymentResponseDTO responseDTO = new PaymentResponseDTO(1, 15, 99, "CARD", new BigDecimal("25000"), LocalDateTime.now());

            when(userDetails.getId()).thenReturn(99);
            when(createPayment.handle(org.mockito.ArgumentMatchers.any(CreatePaymentCommand.class))).thenReturn(payment);
            when(mapper.toResponseDTO(payment)).thenReturn(responseDTO);

            ResponseEntity<PaymentResponseDTO> response = controller.create(requestDTO, userDetails);
            ArgumentCaptor<CreatePaymentCommand> captor = ArgumentCaptor.forClass(CreatePaymentCommand.class);

            verify(createPayment).handle(captor.capture());

            assertThat(captor.getValue().idOrder()).isEqualTo(15);
            assertThat(captor.getValue().idUser()).isEqualTo(99);
            assertThat(captor.getValue().paymentMethod()).isEqualTo(PaymentMethod.CARD);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isSameAs(responseDTO);
        }
    }

    @Nested
    class CartControllerTests {

        @Test
        void shouldSyncCartForAuthenticatedUser() {
            AddCart addCart = mock(AddCart.class);
            GetCart getCart = mock(GetCart.class);
            ClearCart clearCart = mock(ClearCart.class);
            CartRestDtoMapper mapper = mock(CartRestDtoMapper.class);
            CartController controller = new CartController(addCart, getCart, clearCart, mapper);
            CustomUserDetails userDetails = mock(CustomUserDetails.class);
            List<CartItemRequestDTO> requestItems = List.of(new CartItemRequestDTO(null, 1, "Papas", new BigDecimal("6000"), null, 2));
            List<CartItem> domainItems = List.of(mock(CartItem.class));
            Cart cart = mock(Cart.class);
            List<CartItemResponseDTO> responseDTOs = List.of(new CartItemResponseDTO(null, 1, "Papas", new BigDecimal("6000"), null, 2));

            when(userDetails.getId()).thenReturn(44);
            when(mapper.toDomainList(requestItems)).thenReturn(domainItems);
            when(addCart.handle(44, domainItems)).thenReturn(cart);
            when(mapper.toResponseDTOList(cart)).thenReturn(responseDTOs);

            ResponseEntity<List<CartItemResponseDTO>> response = controller.sync(requestItems, userDetails);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).containsExactlyElementsOf(responseDTOs);
        }

        @Test
        void shouldClearCartForAuthenticatedUser() {
            AddCart addCart = mock(AddCart.class);
            GetCart getCart = mock(GetCart.class);
            ClearCart clearCart = mock(ClearCart.class);
            CartRestDtoMapper mapper = mock(CartRestDtoMapper.class);
            CartController controller = new CartController(addCart, getCart, clearCart, mapper);
            CustomUserDetails userDetails = mock(CustomUserDetails.class);

            when(userDetails.getId()).thenReturn(44);

            ResponseEntity<Void> response = controller.clear(userDetails);

            verify(clearCart).handle(44);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class FavoriteBurgerControllerTests {

        @Test
        void shouldAddFavoriteBurger() {
            AddFavoriteBurger addFavoriteBurger = mock(AddFavoriteBurger.class);
            GetBurgerById getBurgerById = mock(GetBurgerById.class);
            RemoveFavoriteBurger removeFavoriteBurger = mock(RemoveFavoriteBurger.class);
            GetFavoriteBurgersByUser getFavoriteBurgersByUser = mock(GetFavoriteBurgersByUser.class);
            FavoriteBurgerRestDtoMapper mapper = mock(FavoriteBurgerRestDtoMapper.class);
            FavoriteBurgerController controller = new FavoriteBurgerController(addFavoriteBurger, getBurgerById, removeFavoriteBurger, getFavoriteBurgersByUser, mapper);
            CustomUserDetails userDetails = mock(CustomUserDetails.class);
            FavoriteBurger favoriteBurger = mock(FavoriteBurger.class);
            FavoriteBurgerSimpleResponseDTO responseDTO = new FavoriteBurgerSimpleResponseDTO(1, 55, 9, "Clásica", LocalDateTime.now());

            when(userDetails.getId()).thenReturn(55);
            when(addFavoriteBurger.handle(55, 9)).thenReturn(favoriteBurger);
            when(mapper.toSimpleResponseDTO(favoriteBurger)).thenReturn(responseDTO);

            ResponseEntity<FavoriteBurgerSimpleResponseDTO> response = controller.add(userDetails, new FavoriteBurgerRequestDTO(9));

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isSameAs(responseDTO);
        }

        @Test
        void shouldBuildDeletionMessageForCustomBurger() {
            AddFavoriteBurger addFavoriteBurger = mock(AddFavoriteBurger.class);
            GetBurgerById getBurgerById = mock(GetBurgerById.class);
            RemoveFavoriteBurger removeFavoriteBurger = mock(RemoveFavoriteBurger.class);
            GetFavoriteBurgersByUser getFavoriteBurgersByUser = mock(GetFavoriteBurgersByUser.class);
            FavoriteBurgerRestDtoMapper mapper = mock(FavoriteBurgerRestDtoMapper.class);
            FavoriteBurgerController controller = new FavoriteBurgerController(addFavoriteBurger, getBurgerById, removeFavoriteBurger, getFavoriteBurgersByUser, mapper);
            CustomUserDetails userDetails = mock(CustomUserDetails.class);
            Burger burger = mock(Burger.class);

            when(userDetails.getId()).thenReturn(55);
            when(getBurgerById.execute(12)).thenReturn(burger);
            when(burger.isOnMenu()).thenReturn(false);
            when(burger.getName()).thenReturn("Mi Burger");

            ResponseEntity<DeleteResponseDTO> response = controller.remove(userDetails, 12);

            verify(removeFavoriteBurger).handle(55, 12);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().message()).isEqualTo("Hamburguesa personalizada eliminada de favoritos y de tus creaciones");
            assertThat(response.getBody().data().id()).isEqualTo(12);
            assertThat(response.getBody().data().name()).isEqualTo("Mi Burger");
        }

        @Test
        void shouldReturnFavoriteBurgerList() {
            AddFavoriteBurger addFavoriteBurger = mock(AddFavoriteBurger.class);
            GetBurgerById getBurgerById = mock(GetBurgerById.class);
            RemoveFavoriteBurger removeFavoriteBurger = mock(RemoveFavoriteBurger.class);
            GetFavoriteBurgersByUser getFavoriteBurgersByUser = mock(GetFavoriteBurgersByUser.class);
            FavoriteBurgerRestDtoMapper mapper = mock(FavoriteBurgerRestDtoMapper.class);
            FavoriteBurgerController controller = new FavoriteBurgerController(addFavoriteBurger, getBurgerById, removeFavoriteBurger, getFavoriteBurgersByUser, mapper);
            CustomUserDetails userDetails = mock(CustomUserDetails.class);
            List<FavoriteBurgerDetail> details = List.of(mock(FavoriteBurgerDetail.class));
            List<FavoriteBurgerResponseDTO> responseDTOs = List.of(new FavoriteBurgerResponseDTO(1, 55, null, LocalDateTime.now()));

            when(userDetails.getId()).thenReturn(55);
            when(getFavoriteBurgersByUser.handle(55)).thenReturn(details);
            when(mapper.toResponseDTOList(details)).thenReturn(responseDTOs);

            ResponseEntity<List<FavoriteBurgerResponseDTO>> response = controller.getAll(userDetails);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).containsExactlyElementsOf(responseDTOs);
        }
    }

    @Nested
    class WhatsappSettingsControllerTests {

        @Test
        void shouldUpdateWhatsappSettings() {
            GetWhatsappSettings getSettings = mock(GetWhatsappSettings.class);
            UpdateWhatsappSettings updateSettings = mock(UpdateWhatsappSettings.class);
            WhatsappSettingsRestMapper mapper = mock(WhatsappSettingsRestMapper.class);
            WhatsappSettingsController controller = new WhatsappSettingsController(getSettings, updateSettings, mapper);
            UpdateWhatsappSettingsRequestDTO requestDTO = new UpdateWhatsappSettingsRequestDTO("573001112233", "secret", "Hola", true);
            WhatsappSettings settings = mock(WhatsappSettings.class);
            WhatsappSettingsResponseDTO responseDTO = new WhatsappSettingsResponseDTO(1, "573001112233", "secret", "Hola", true, LocalDateTime.now());

            when(updateSettings.handle("573001112233", "secret", "Hola", true)).thenReturn(settings);
            when(mapper.toResponseDTO(settings)).thenReturn(responseDTO);

            ResponseEntity<WhatsappSettingsResponseDTO> response = controller.update(requestDTO);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isSameAs(responseDTO);
        }
    }

    @Nested
    class InvoiceControllerTests {

        @Test
        void shouldReturnInvoicePdfUrl() {
            GetInvoiceByOrder getInvoiceByOrder = mock(GetInvoiceByOrder.class);
            InvoiceRestDtoMapper mapper = mock(InvoiceRestDtoMapper.class);
            InvoiceController controller = new InvoiceController(getInvoiceByOrder, mapper);
            Invoice invoice = mock(Invoice.class);

            when(getInvoiceByOrder.handle(30)).thenReturn(invoice);
            when(invoice.getPdfUrl()).thenReturn("https://cdn.test/invoice.pdf");

            ResponseEntity<String> response = controller.getPdf(30);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo("https://cdn.test/invoice.pdf");
        }
    }

    @Nested
    class MenuCategoryControllerTests {

        @Test
        void shouldCreateMenuCategory() {
            CreateMenuCategory createMenuCategory = mock(CreateMenuCategory.class);
            UpdateMenuCategory updateMenuCategory = mock(UpdateMenuCategory.class);
            DeleteMenuCategory deleteMenuCategory = mock(DeleteMenuCategory.class);
            GetMenuCategoryById getMenuCategoryById = mock(GetMenuCategoryById.class);
            ListMenuCategory listMenuCategory = mock(ListMenuCategory.class);
            MenuCategoryDtoMapper mapper = mock(MenuCategoryDtoMapper.class);
            MenuCategoryController controller = new MenuCategoryController(createMenuCategory, updateMenuCategory, deleteMenuCategory, getMenuCategoryById, listMenuCategory, mapper);
            CustomUserDetails userDetails = mock(CustomUserDetails.class);
            CreateMenuCategoryRequestDTO requestDTO = new CreateMenuCategoryRequestDTO("Promos", "Combos especiales");
            MenuCategory category = mock(MenuCategory.class);
            MenuCategoryResponseDTO responseDTO = new MenuCategoryResponseDTO(1, "Promos", "Combos especiales", LocalDateTime.now(), null, 99, null);

            when(userDetails.getId()).thenReturn(99);
            when(mapper.toCreateCommand(requestDTO, 99)).thenReturn(null);
            when(createMenuCategory.create(null)).thenReturn(category);
            when(mapper.toResponseDTO(category)).thenReturn(responseDTO);

            ResponseEntity<MenuCategoryResponseDTO> response = controller.create(requestDTO, userDetails);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isSameAs(responseDTO);
        }

        @Test
        void shouldDeleteMenuCategoryAndReturnDeletedResource() {
            CreateMenuCategory createMenuCategory = mock(CreateMenuCategory.class);
            UpdateMenuCategory updateMenuCategory = mock(UpdateMenuCategory.class);
            DeleteMenuCategory deleteMenuCategory = mock(DeleteMenuCategory.class);
            GetMenuCategoryById getMenuCategoryById = mock(GetMenuCategoryById.class);
            ListMenuCategory listMenuCategory = mock(ListMenuCategory.class);
            MenuCategoryDtoMapper mapper = mock(MenuCategoryDtoMapper.class);
            MenuCategoryController controller = new MenuCategoryController(createMenuCategory, updateMenuCategory, deleteMenuCategory, getMenuCategoryById, listMenuCategory, mapper);
            CustomUserDetails userDetails = mock(CustomUserDetails.class);
            MenuCategory category = mock(MenuCategory.class);

            when(userDetails.getId()).thenReturn(99);
            when(deleteMenuCategory.handle(6, 99)).thenReturn(category);
            when(category.getIdMenuCategory()).thenReturn(6);
            when(category.getMenuCategoryName()).thenReturn("Promos");

            ResponseEntity<DeleteResponseDTO> response = controller.delete(6, userDetails);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().message()).isEqualTo("Categoría eliminada correctamente");
            assertThat(response.getBody().data().id()).isEqualTo(6);
            assertThat(response.getBody().data().name()).isEqualTo("Promos");
        }
    }

    @Nested
    class SupplierControllerTests {

        @Test
        void shouldListSuppliersUsingMapper() {
            CreateSupplier createSupplier = mock(CreateSupplier.class);
            UpdateSupplier updateSupplier = mock(UpdateSupplier.class);
            DeleteSupplier deleteSupplier = mock(DeleteSupplier.class);
            GetSupplierById getSupplierById = mock(GetSupplierById.class);
            ListSuppliers listSuppliers = mock(ListSuppliers.class);
            SupplierRestDtoMapper mapper = mock(SupplierRestDtoMapper.class);
            SupplierController controller = new SupplierController(createSupplier, updateSupplier, deleteSupplier, getSupplierById, listSuppliers, mapper);
            PageResponse<Supplier> suppliersPage = new PageResponse<>(List.of(mock(Supplier.class)), 0, 12, 1, 1);
            ListSupplierResponseDTO responseDTO = ListSupplierResponseDTO.builder()
                    .items(List.of(SupplierResponseDTO.builder().id(1).name("Proveedor").build()))
                    .page(0)
                    .size(12)
                    .totalElements(1)
                    .totalPages(1)
                    .build();

            when(listSuppliers.list(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(suppliersPage);
            when(mapper.toListResponseDTO(suppliersPage)).thenReturn(responseDTO);

            ResponseEntity<ListSupplierResponseDTO> response = controller.list("pro", 0, 12, "name", "ASC");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isSameAs(responseDTO);
        }
    }

    @Nested
    class ProductCategoryControllerTests {

        @Test
        void shouldReturnPublicProductCategories() {
            CreateProductCategory createCategory = mock(CreateProductCategory.class);
            UpdateProductCategory updateCategory = mock(UpdateProductCategory.class);
            DeleteProductCategory deleteCategory = mock(DeleteProductCategory.class);
            GetProductCategoryById getById = mock(GetProductCategoryById.class);
            ListProductCategories listCategories = mock(ListProductCategories.class);
            ListPublicProductCategories publicProductCategories = mock(ListPublicProductCategories.class);
            ProductCategoryRestDtoMapper mapper = mock(ProductCategoryRestDtoMapper.class);
            ProductCategoryController controller = new ProductCategoryController(createCategory, updateCategory, deleteCategory, getById, listCategories, publicProductCategories, mapper);
            ProductCategory category = mock(ProductCategory.class);
            ProductCategoryResponseDTO responseDTO = ProductCategoryResponseDTO.builder()
                    .id(1)
                    .name("Bebidas")
                    .description("Categoría pública")
                    .available(true)
                    .build();

            when(publicProductCategories.execute()).thenReturn(List.of(category));
            when(mapper.toResponseDTO(category)).thenReturn(responseDTO);

            ResponseEntity<List<ProductCategoryResponseDTO>> response = controller.getPublicCategories();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).containsExactly(responseDTO);
        }

        @Test
        void shouldListProductCategories() {
            CreateProductCategory createCategory = mock(CreateProductCategory.class);
            UpdateProductCategory updateCategory = mock(UpdateProductCategory.class);
            DeleteProductCategory deleteCategory = mock(DeleteProductCategory.class);
            GetProductCategoryById getById = mock(GetProductCategoryById.class);
            ListProductCategories listCategories = mock(ListProductCategories.class);
            ListPublicProductCategories publicProductCategories = mock(ListPublicProductCategories.class);
            ProductCategoryRestDtoMapper mapper = mock(ProductCategoryRestDtoMapper.class);
            ProductCategoryController controller = new ProductCategoryController(createCategory, updateCategory, deleteCategory, getById, listCategories, publicProductCategories, mapper);
            PageResponse<ProductCategory> categoriesPage = new PageResponse<>(List.of(mock(ProductCategory.class)), 0, 12, 1, 1);
            ListProductCategoryResponseDTO responseDTO = ListProductCategoryResponseDTO.builder()
                    .items(List.of(ProductCategoryResponseDTO.builder().id(1).name("Bebidas").build()))
                    .page(0)
                    .size(12)
                    .totalElements(1)
                    .totalPages(1)
                    .build();

            when(listCategories.list(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).thenReturn(categoriesPage);
            when(mapper.toListResponseDTO(categoriesPage)).thenReturn(responseDTO);

            ResponseEntity<ListProductCategoryResponseDTO> response = controller.list("beb", 0, 12, "name", "ASC");

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isSameAs(responseDTO);
        }
    }

    private static final class TestAuthenticatedController extends AuthenticatedController {
        private Integer extract(UserDetails userDetails) {
            return getUserId(userDetails);
        }
    }
}
