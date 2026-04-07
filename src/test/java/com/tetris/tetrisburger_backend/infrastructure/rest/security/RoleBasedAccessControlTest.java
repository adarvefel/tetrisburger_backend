package com.tetris.tetrisburger_backend.infrastructure.rest.security;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.BurgerSettings;
import com.tetris.tetrisburger_backend.domain.model.Menu;
import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.*;
import com.tetris.tetrisburger_backend.domain.port.in.additionsettings.GetAdditionSettings;
import com.tetris.tetrisburger_backend.domain.port.in.additionsettings.UpdateAdditionSettings;
import com.tetris.tetrisburger_backend.domain.port.in.auth.ForgotPassword;
import com.tetris.tetrisburger_backend.domain.port.in.auth.LoginUser;
import com.tetris.tetrisburger_backend.domain.port.in.auth.LoginWithGoogle;
import com.tetris.tetrisburger_backend.domain.port.in.auth.ResetPassword;
import com.tetris.tetrisburger_backend.domain.common.LoginResponse;
import com.tetris.tetrisburger_backend.domain.port.in.burger.*;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.*;
import com.tetris.tetrisburger_backend.domain.port.in.burger.client.CreateCustomBurger;
import com.tetris.tetrisburger_backend.domain.port.in.cart.AddCart;
import com.tetris.tetrisburger_backend.domain.port.in.cart.ClearCart;
import com.tetris.tetrisburger_backend.domain.port.in.cart.GetCart;
import com.tetris.tetrisburger_backend.domain.port.in.favoriteburger.AddFavoriteBurger;
import com.tetris.tetrisburger_backend.domain.port.in.favoriteburger.GetFavoriteBurgersByUser;
import com.tetris.tetrisburger_backend.domain.port.in.favoriteburger.RemoveFavoriteBurger;
import com.tetris.tetrisburger_backend.domain.port.in.invoice.GetInvoiceByOrder;
import com.tetris.tetrisburger_backend.domain.port.in.menu.*;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.*;
import com.tetris.tetrisburger_backend.domain.port.in.order.*;
import com.tetris.tetrisburger_backend.domain.port.in.payment.CreatePayment;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.*;
import com.tetris.tetrisburger_backend.domain.port.in.product.*;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.*;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.*;
import com.tetris.tetrisburger_backend.domain.port.in.user.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.infrastructure.config.RateLimitConfig;
import com.tetris.tetrisburger_backend.infrastructure.config.SecurityConfig;
import com.tetris.tetrisburger_backend.infrastructure.rest.controller.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.validator.ImageValidator;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import com.tetris.tetrisburger_backend.infrastructure.security.JwtAuthenticationEntryPoint;
import com.tetris.tetrisburger_backend.infrastructure.security.JwtAuthenticationFilter;
import com.tetris.tetrisburger_backend.infrastructure.security.JwtUtil;
import com.tetris.tetrisburger_backend.infrastructure.security.RateLimitFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica la política de {@link SecurityConfig} y {@code @PreAuthorize} en controllers.
 * Rutas y métodos alineados con {@code SecurityConfig} y los controllers reales (/api/...).
 */
@WebMvcTest(controllers = {
        AdditionController.class,
        AdditionSettingsController.class,
        AdminBurgerController.class,
        AdminController.class,
        AdminSettingsController.class,
        AuthController.class,
        CartController.class,
        FavoriteBurgerController.class,
        InvoiceController.class,
        MenuCategoryController.class,
        MenuController.class,
        OrderController.class,
        PaymentController.class,
        PqrsController.class,
        ProductCategoryController.class,
        ProductController.class,
        ProfileController.class,
        SupplierController.class,
        UserBurgerController.class
})
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAuthenticationEntryPoint.class,
        RoleBasedAccessControlTest.RateLimitBypassConfig.class
})
@TestPropertySource(properties = "cors.allowed.origins=http://localhost")
@DisplayName("RBAC — SecurityConfig y roles")
class RoleBasedAccessControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    // AdditionController
    @MockitoBean
    private CreateAddition createAddition;
    @MockitoBean
    private UpdateAddition updateAddition;
    @MockitoBean
    private UpdateAdditionImage updateAdditionImage;
    @MockitoBean
    private ListAddition listAddition;
    @MockitoBean
    private SearchAdditionByName searchAdditionByName;
    @MockitoBean
    private GetAdditionById getAdditionById;
    @MockitoBean
    private DeleteAddition deleteAddition;
    @MockitoBean
    private AdditionRestDtoMapper additionRestDtoMapper;

    // AdditionSettingsController
    @MockitoBean
    private GetAdditionSettings getAdditionSettings;
    @MockitoBean
    private UpdateAdditionSettings updateAdditionSettings;
    @MockitoBean
    private AdditionSettingsDtoMapper additionSettingsDtoMapper;

    // AdminBurgerController
    @MockitoBean
    private CreateMenuBurger createMenuBurger;
    @MockitoBean
    private ListBurgers listBurgers;
    @MockitoBean
    private UpdateMenuBurger updateMenuBurger;
    @MockitoBean
    private UpdateMenuBurgerImage updateMenuBurgerImage;
    @MockitoBean
    private DeleteMenuBurger deleteMenuBurger;
    @MockitoBean
    private SearchMenuBurgers searchMenuBurgers;
    @MockitoBean
    private GetBurgerById getBurgerById;
    @MockitoBean
    private ListBurgerIngredients listBurgerIngredients;
    @MockitoBean
    private SearchIngredients searchIngredients;
    @MockitoBean
    private BurgerRestDtoMapper burgerRestDtoMapper;
    @MockitoBean
    private ProductRestDtoMapper productRestDtoMapper;
    @MockitoBean
    private ImageValidator imageValidator;

    // AdminController
    @MockitoBean
    private CreateUserByAdmin createUserByAdmin;
    @MockitoBean
    private ListUser listUser;
    @MockitoBean
    private GetUserById getUserById;
    @MockitoBean
    private UpdateUserByAdmin updateUserByAdmin;
    @MockitoBean
    private UpdateUserImageByAdmin updateUserImageByAdmin;
    @MockitoBean
    private DeleteUserByAdmin deleteUserByAdmin;
    @MockitoBean
    private SearchUsersByEmail searchUsersByEmail;
    @MockitoBean
    private UserRestDtoMapper userRestDtoMapper;
    @MockitoBean
    private ImageStoragePort imageStoragePort;

    // AdminSettingsController
    @MockitoBean
    private GetBurgerSettings getBurgerSettings;
    @MockitoBean
    private UpdateBurgerSettings updateBurgerSettings;

    // AuthController
    @MockitoBean
    private RegisterUser registerUser;
    @MockitoBean
    private LoginUser loginUser;
    @MockitoBean
    private LoginWithGoogle loginWithGoogle;
    @MockitoBean
    private ForgotPassword forgotPassword;
    @MockitoBean
    private ResetPassword resetPassword;
    @MockitoBean
    private AuthRestDtoMapper authRestDtoMapper;

    // CartController
    @MockitoBean
    private AddCart addCart;
    @MockitoBean
    private GetCart getCart;
    @MockitoBean
    private ClearCart clearCart;
    @MockitoBean
    private CartRestDtoMapper cartRestDtoMapper;

    // FavoriteBurgerController (+ GetBurgerById)
    @MockitoBean
    private AddFavoriteBurger addFavoriteBurger;
    @MockitoBean
    private RemoveFavoriteBurger removeFavoriteBurger;
    @MockitoBean
    private GetFavoriteBurgersByUser getFavoriteBurgersByUser;
    @MockitoBean
    private FavoriteBurgerRestDtoMapper favoriteBurgerRestDtoMapper;

    // InvoiceController
    @MockitoBean
    private GetInvoiceByOrder getInvoiceByOrder;
    @MockitoBean
    private InvoiceRestDtoMapper invoiceRestDtoMapper;

    // MenuCategoryController
    @MockitoBean
    private CreateMenuCategory createMenuCategory;
    @MockitoBean
    private UpdateMenuCategory updateMenuCategory;
    @MockitoBean
    private DeleteMenuCategory deleteMenuCategory;
    @MockitoBean
    private GetMenuCategoryById getMenuCategoryById;
    @MockitoBean
    private ListMenuCategory listMenuCategory;
    @MockitoBean
    private MenuCategoryDtoMapper menuCategoryDtoMapper;

    // MenuController
    @MockitoBean
    private CreateMenu createMenu;
    @MockitoBean
    private UpdateMenu updateMenu;
    @MockitoBean
    private DeleteMenu deleteMenu;
    @MockitoBean
    private GetMenuById getMenuById;
    @MockitoBean
    private ListMenu listMenu;
    @MockitoBean
    private UpdateMenuImage updateMenuImage;
    @MockitoBean
    private MenuRestDtoMapper menuRestDtoMapper;

    // OrderController
    @MockitoBean
    private CreateOrder createOrder;
    @MockitoBean
    private GetOrderById getOrderById;
    @MockitoBean
    private ListUserOrders listUserOrders;
    @MockitoBean
    private ListAllOrders listAllOrders;
    @MockitoBean
    private UpdateOrderStatus updateOrderStatus;
    @MockitoBean
    private SearchOrderByNumber searchOrderByNumber;
    @MockitoBean
    private OrderRestDtoMapper orderRestDtoMapper;

    // PaymentController
    @MockitoBean
    private CreatePayment createPayment;
    @MockitoBean
    private PaymentRestDtoMapper paymentRestDtoMapper;

    // PqrsController
    @MockitoBean
    private PqrsRestDtoMapper pqrsRestDtoMapper;
    @MockitoBean
    private CreatePqrs createPqrs;
    @MockitoBean
    private GetPqrsById getPqrsById;
    @MockitoBean
    private ListPqrs listPqrs;
    @MockitoBean
    private DeleteSoftPqrs deleteSoftPqrs;
    @MockitoBean
    private UpdatePqrs updatePqrs;
    @MockitoBean
    private UpdatePqrsByAdmin updatePqrsByAdmin;
    @MockitoBean
    private ListPqrsById listPqrsById;

    // ProductCategoryController
    @MockitoBean
    private CreateProductCategory createProductCategory;
    @MockitoBean
    private DeleteProductCategory deleteProductCategory;
    @MockitoBean
    private GetProductCategoryById getProductCategoryById;
    @MockitoBean
    private ListProductCategories listProductCategories;
    @MockitoBean
    private ListPublicProductCategories listPublicProductCategories;
    @MockitoBean
    private UpdateProductCategory updateProductCategory;
    @MockitoBean
    private ProductCategoryRestDtoMapper productCategoryRestDtoMapper;

    // ProductController
    @MockitoBean
    private CreateProduct createProduct;
    @MockitoBean
    private ListProducts listProducts;
    @MockitoBean
    private GetProductById getProductById;
    @MockitoBean
    private UpdateProduct updateProduct;
    @MockitoBean
    private UpdateProductImage updateProductImage;
    @MockitoBean
    private DeleteProduct deleteProduct;
    @MockitoBean
    private SearchProducts searchProducts;
    @MockitoBean
    private SetProductAvailability setProductAvailability;
    @MockitoBean
    private AdjustProductStock adjustProductStock;
    @MockitoBean
    private ListPublicProducts listPublicProducts;

    // ProfileController
    @MockitoBean
    private GetUserProfile getUserProfile;
    @MockitoBean
    private UpdateProfileUser updateProfileUser;
    @MockitoBean
    private UpdateProfileImage updateProfileImage;
    @MockitoBean
    private DeleteProfileUser deleteProfileUser;

    // SupplierController
    @MockitoBean
    private CreateSupplier createSupplier;
    @MockitoBean
    private UpdateSupplier updateSupplier;
    @MockitoBean
    private DeleteSupplier deleteSupplier;
    @MockitoBean
    private GetSupplierById getSupplierById;
    @MockitoBean
    private ListSuppliers listSuppliers;
    @MockitoBean
    private SupplierRestDtoMapper supplierRestDtoMapper;

    // UserBurgerController (comparte GetBurgerById con Favorite)
    @MockitoBean
    private CreateCustomBurger createCustomBurger;
    @MockitoBean
    private UpdateCustomBurger updateCustomBurger;
    @MockitoBean
    private GetFeaturedBurgers getFeaturedBurgers;

    @TestConfiguration
    static class RateLimitBypassConfig {
        @Bean
        @Primary
        RateLimitFilter rateLimitFilter() {
            return new RateLimitFilter(mock(RateLimitConfig.class), mock(ObjectMapper.class)) {
                @Override
                protected void doFilterInternal(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain filterChain
                ) throws ServletException, IOException {
                    filterChain.doFilter(request, response);
                }
            };
        }
    }

    private static CustomUserDetails clientPrincipal() {
        return new CustomUserDetails(
                1,
                "client@test.com",
                "pw",
                List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );
    }

    private static CustomUserDetails adminPrincipal() {
        return new CustomUserDetails(
                1,
                "admin@test.com",
                "pw",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }

    private static CustomUserDetails employeePrincipal() {
        return new CustomUserDetails(
                2,
                "emp@test.com",
                "pw",
                List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))
        );
    }

    @BeforeEach
    void defaultStubs() throws Exception {
        lenient().when(userDetailsService.loadUserByUsername(any()))
                .thenAnswer(inv -> {
                    String username = inv.getArgument(0);
                    if (username == null || username.isBlank()) {
                        username = "stub-user";
                    }
                    return new org.springframework.security.core.userdetails.User(
                            username,
                            "pw",
                            List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))
                    );
                });

        lenient().when(listUser.execute(any())).thenReturn(new PageResponse<>(List.of(), 0, 10, 0L, 0));
        lenient().when(getBurgerSettings.handle()).thenReturn(BurgerSettings.createDefaults());
        lenient().when(listProducts.list(any(), any())).thenReturn(new PageResponse<>(List.of(), 0, 10, 0L, 0));
        lenient().when(listSuppliers.list(any(), any())).thenReturn(new PageResponse<>(List.of(), 0, 10, 0L, 0));
        lenient().when(getSupplierById.get(any())).thenReturn(mock(com.tetris.tetrisburger_backend.domain.model.Supplier.class));
        lenient().when(supplierRestDtoMapper.toResponseDTO(any())).thenReturn(mock());

        User userMock = mock(User.class);
        lenient().when(getUserProfile.execute(any())).thenReturn(userMock);
        lenient().when(userRestDtoMapper.toGetUserProfileResponseDTO(any(), any(), any())).thenReturn(mock());

        Order orderMock = mock(Order.class);
        lenient().when(orderMock.getItems()).thenReturn(List.of());
        lenient().when(createOrder.handle(anyInt(), anyList())).thenReturn(orderMock);
        lenient().when(orderRestDtoMapper.toResponseDTO(any())).thenReturn(mock());

        lenient().when(listUserOrders.handle(anyInt(), any())).thenReturn(new PageResponse<>(List.of(), 0, 10, 0L, 0));

        Menu menuMock = mock(Menu.class);
        lenient().when(menuMock.getIdMenu()).thenReturn(1);
        lenient().when(menuMock.getName()).thenReturn("Menu");
        lenient().when(createMenu.handle(any())).thenReturn(menuMock);
        lenient().when(menuRestDtoMapper.toResponseDTO(any())).thenReturn(mock());

        lenient().when(forgotPassword.execute(any())).thenReturn("ok");

        User googleUser = mock(User.class);
        LoginResponse loginResponse = new LoginResponse("token", googleUser, 3600L);
        lenient().when(loginWithGoogle.handle(any())).thenReturn(loginResponse);
        lenient().when(authRestDtoMapper.toLoginResponseDTO(any())).thenReturn(mock());
        lenient().when(authRestDtoMapper.toLoginWithGoogleCommand(any())).thenReturn(mock());
        lenient().when(authRestDtoMapper.toLoginCommand(any())).thenReturn(mock());
        lenient().when(authRestDtoMapper.toRegisterCommand(any())).thenReturn(mock());
        lenient().when(authRestDtoMapper.toForgotPasswordCommand(any())).thenReturn(mock());
        lenient().when(authRestDtoMapper.toResetPasswordCommand(any())).thenReturn(mock());

        lenient().doNothing().when(resetPassword).handle(any());
    }

    @Nested
    @DisplayName("Rutas públicas (SecurityConfig permitAll / GET permitidos)")
    class PublicEndpointsTests {

        @Test
        void postAuthLogin_shouldNotReturn403WithoutToken() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void postAuthRegister_shouldBeReachableWithoutToken() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void postAuthForgotPassword_shouldBeReachableWithoutToken() throws Exception {
            mockMvc.perform(post("/api/auth/forgot-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"a@b.com\"}"))
                    .andExpect(status().isOk());
        }

        @Test
        void postAuthResetPassword_shouldBeReachableWithoutToken() throws Exception {
            mockMvc.perform(post("/api/auth/reset-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"token\":\"t\",\"newPassword\":\"secret12\"}"))
                    .andExpect(status().isOk());
        }

        @Test
        void postAuthGoogle_shouldBeReachableWithoutToken() throws Exception {
            mockMvc.perform(post("/api/auth/google")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"token\":\"google\"}"))
                    .andExpect(status().isOk());
        }

        @Test
        void getProductsPublic_shouldBePermitAll() throws Exception {
            when(listPublicProducts.list(isNull(), isNull(), any())).thenReturn(new PageResponse<>(List.of(), 0, 10, 0L, 0));
            mockMvc.perform(get("/api/products/public"))
                    .andExpect(status().isOk());
        }

        @Test
        void getProductCategoriesPublic_shouldBePermitAll() throws Exception {
            when(listPublicProductCategories.execute()).thenReturn(List.of());
            mockMvc.perform(get("/api/product-categories/public"))
                    .andExpect(status().isOk());
        }

        @Test
        void getBurgersFeatured_shouldBePermitAll() throws Exception {
            when(getFeaturedBurgers.handle()).thenReturn(List.of());
            mockMvc.perform(get("/api/burgers/featured"))
                    .andExpect(status().isOk());
        }

        @Test
        void getCart_shouldNotBe401_perSecurityConfigPermitAll() throws Exception {
            // SecurityConfig: /api/cart/** permitAll — el filtro no devuelve 401 aunque el controller falle sin principal
            mockMvc.perform(get("/api/cart"))
                    .andExpect(result -> {
                        int sc = result.getResponse().getStatus();
                        if (sc == 401) {
                            throw new AssertionError("GET /api/cart no debe ser 401 con la política actual (permitAll)");
                        }
                    });
        }

        @Test
        void getAdminAdditions_shouldBeGetPermitAll() throws Exception {
            when(listAddition.handle(isNull(), any())).thenReturn(new PageResponse<>(List.of(), 0, 10, 0L, 0));
            mockMvc.perform(get("/api/admin/additions"))
                    .andExpect(status().isOk());
        }

        @Test
        void getMenuList_shouldBeGetPermitAll() throws Exception {
            when(listMenu.handle(any())).thenReturn(new PageResponse<>(List.of(), 0, 12, 0L, 0));
            mockMvc.perform(get("/api/menu"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Sin autenticación → 401 (anyRequest().authenticated() o matcher de rol)")
    class UnauthenticatedAccessTests {

        @Test
        void getProfile_shouldReturn401() throws Exception {
            mockMvc.perform(get("/api/profile"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void patchProfile_shouldReturn401() throws Exception {
            mockMvc.perform(patch("/api/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"userName\":\"Nombre Largo\"}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void getFavorites_shouldReturn401() throws Exception {
            mockMvc.perform(get("/api/favorites"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void postOrders_shouldReturn401() throws Exception {
            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"items\":[]}"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void getMyOrders_shouldReturn401() throws Exception {
            mockMvc.perform(get("/api/orders/my-orders"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void getSupplierById_shouldReturn401() throws Exception {
            mockMvc.perform(get("/api/suppliers/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void getAdminUsers_shouldReturn401() throws Exception {
            mockMvc.perform(get("/api/admin/users"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void postAdminAdditions_shouldRequireAuthentication() throws Exception {
            mockMvc.perform(multipart("/api/admin/additions"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Rol CLIENT — prohibido en recursos solo ADMIN / empleado")
    class ClientRoleAccessTests {

        @Test
        void getAdminUsers_shouldReturn403() throws Exception {
            mockMvc.perform(get("/api/admin/users").with(user(clientPrincipal())))
                    .andExpect(status().isForbidden());
        }

        @Test
        void postAdminUsers_shouldReturn403() throws Exception {
            mockMvc.perform(multipart("/api/admin/users")
                            .file(new org.springframework.mock.web.MockMultipartFile(
                                    "data", "", MediaType.APPLICATION_JSON_VALUE,
                                    "{\"userName\":\"Juan\",\"email\":\"j@j.com\",\"password\":\"secret12\",\"role\":\"CLIENT\"}".getBytes()
                            )).with(user(clientPrincipal())))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteAdminUser_shouldReturn403() throws Exception {
            mockMvc.perform(delete("/api/admin/users/1").with(user(clientPrincipal())))
                    .andExpect(status().isForbidden());
        }

        @Test
        void getAdminAdditionById_shouldReturn403_whenClient() throws Exception {
            mockMvc.perform(get("/api/admin/additions/1").with(user(clientPrincipal())))
                    .andExpect(status().isForbidden());
        }

        @Test
        void getAdminSettingsBurgers_shouldReturn403() throws Exception {
            mockMvc.perform(get("/api/admin/settings/burgers").with(user(clientPrincipal())))
                    .andExpect(status().isForbidden());
        }

        @Test
        void getAdminBurgerById_shouldReturn403_whenClient() throws Exception {
            mockMvc.perform(get("/api/admin/burgers/1").with(user(clientPrincipal())))
                    .andExpect(status().isForbidden());
        }

        @Test
        void getOrdersAll_shouldReturn403() throws Exception {
            mockMvc.perform(get("/api/orders/all").with(user(clientPrincipal())))
                    .andExpect(status().isForbidden());
        }

        @Test
        void patchOrderStatus_shouldReturn403() throws Exception {
            mockMvc.perform(patch("/api/orders/1/status")
                            .param("status", "COMPLETED")
                            .with(user(clientPrincipal())))
                    .andExpect(status().isForbidden());
        }

        @Test
        void getPqrsAdminList_shouldReturn403() throws Exception {
            mockMvc.perform(get("/api/pqrs").with(user(clientPrincipal())))
                    .andExpect(status().isForbidden());
        }

        @Test
        void postPayment_shouldReturn403() throws Exception {
            mockMvc.perform(post("/api/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"idOrder\":1,\"paymentMethod\":\"CARD\"}")
                            .with(user(clientPrincipal())))
                    .andExpect(status().isForbidden());
        }

        @Test
        void getAdditionSettings_shouldReturn403() throws Exception {
            mockMvc.perform(get("/api/addition-settings").with(user(clientPrincipal())))
                    .andExpect(status().isForbidden());
        }

        @Test
        void getProfile_shouldReturn200() throws Exception {
            mockMvc.perform(get("/api/profile").with(user(clientPrincipal())))
                    .andExpect(status().isOk());
        }

        @Test
        void postOrders_shouldReturn201_whenClient() throws Exception {
            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"items\":[]}")
                            .with(user(clientPrincipal())))
                    .andExpect(status().isCreated());
        }

        @Test
        void getMyOrders_shouldReturn200_whenClient() throws Exception {
            mockMvc.perform(get("/api/orders/my-orders").with(user(clientPrincipal())))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Rol ADMIN — acceso a /api/admin/users y settings")
    class AdminRoleAccessTests {

        @Test
        void getAdminUsers_shouldReturn200() throws Exception {
            mockMvc.perform(get("/api/admin/users").with(user(adminPrincipal())))
                    .andExpect(status().isOk());
        }

        @Test
        void getAdminSettingsBurgers_shouldReturn200() throws Exception {
            mockMvc.perform(get("/api/admin/settings/burgers").with(user(adminPrincipal())))
                    .andExpect(status().isOk());
        }

        @Test
        void getOrdersAll_shouldReturn200() throws Exception {
            when(listAllOrders.handle(any(), any(), any())).thenReturn(new PageResponse<>(List.of(), 0, 10, 0L, 0));
            mockMvc.perform(get("/api/orders/all").with(user(adminPrincipal())))
                    .andExpect(status().isOk());
        }

        @Test
        void postMenu_shouldReturn201_whenAdmin() throws Exception {
            mockMvc.perform(multipart("/api/menu")
                            .file(new org.springframework.mock.web.MockMultipartFile(
                                    "data", "", MediaType.APPLICATION_JSON_VALUE,
                                    "{\"name\":\"M\",\"description\":null,\"isAvailable\":true,\"idMenuCategory\":1,\"items\":[]}".getBytes()
                            )).with(user(adminPrincipal())))
                    .andExpect(status().isCreated());
        }

        @Test
        void postSupplier_shouldReturn201_whenAdmin() throws Exception {
            lenient().when(createSupplier.create(any())).thenReturn(mock(com.tetris.tetrisburger_backend.domain.model.Supplier.class));
            lenient().when(supplierRestDtoMapper.toResponseDTO(any())).thenReturn(mock());
            mockMvc.perform(post("/api/suppliers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Proveedor\",\"email\":\"s@s.com\",\"phone\":\"300\"}")
                            .with(user(adminPrincipal())))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("Rol EMPLOYEE — mismos matchers que ADMIN en órdenes y addition-settings")
    class EmployeeRoleAccessTests {

        @Test
        void getOrdersAll_shouldReturn200() throws Exception {
            when(listAllOrders.handle(any(), any(), any())).thenReturn(new PageResponse<>(List.of(), 0, 10, 0L, 0));
            mockMvc.perform(get("/api/orders/all").with(user(employeePrincipal())))
                    .andExpect(status().isOk());
        }

        @Test
        void getAdditionSettings_shouldReturn200() throws Exception {
            when(getAdditionSettings.handle()).thenReturn(mock(com.tetris.tetrisburger_backend.domain.model.AdditionSettings.class));
            when(additionSettingsDtoMapper.toResponseDTO(any())).thenReturn(mock());
            mockMvc.perform(get("/api/addition-settings").with(user(employeePrincipal())))
                    .andExpect(status().isOk());
        }
    }
}
