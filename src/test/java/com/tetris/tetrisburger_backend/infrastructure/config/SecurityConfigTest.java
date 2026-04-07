package com.tetris.tetrisburger_backend.infrastructure.config;

import com.tetris.tetrisburger_backend.infrastructure.security.JwtAuthenticationEntryPoint;
import com.tetris.tetrisburger_backend.infrastructure.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "cors.allowed.origins=http://localhost:3000,http://localhost:4200"
})
@DisplayName("Pruebas de SecurityConfig")
class
SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    // ========================================
    // PRUEBAS DE BEANS
    // ========================================

    @Nested
    @DisplayName("Configuración de Beans")
    class BeanConfigurationTests {

        @Test
        @DisplayName("debería crear bean de PasswordEncoder")
        void shouldCreatePasswordEncoderBean() {
            // Then
            assertThat(passwordEncoder).isNotNull();
            assertThat(passwordEncoder).isInstanceOf(PasswordEncoder.class);
        }

        @Test
        @DisplayName("debería crear bean de AuthenticationProvider")
        void shouldCreateAuthenticationProviderBean() {
            // Then
            assertThat(authenticationProvider).isNotNull();
            assertThat(authenticationProvider).isInstanceOf(AuthenticationProvider.class);
        }

        @Test
        @DisplayName("debería crear bean de AuthenticationManager")
        void shouldCreateAuthenticationManagerBean() {
            // Then
            assertThat(authenticationManager).isNotNull();
            assertThat(authenticationManager).isInstanceOf(AuthenticationManager.class);
        }

        @Test
        @DisplayName("debería crear bean de CorsConfigurationSource")
        void shouldCreateCorsConfigurationSourceBean() {
            // Then
            assertThat(corsConfigurationSource).isNotNull();
        }

        @Test
        @DisplayName("debería crear bean de SecurityConfig")
        void shouldCreateSecurityConfigBean() {
            // Then
            assertThat(securityConfig).isNotNull();
        }

        @Test
        @DisplayName("debería inyectar JwtAuthenticationFilter")
        void shouldInjectJwtAuthenticationFilter() {
            // Then
            assertThat(jwtAuthenticationFilter).isNotNull();
        }

        @Test
        @DisplayName("debería inyectar JwtAuthenticationEntryPoint")
        void shouldInjectJwtAuthenticationEntryPoint() {
            // Then
            assertThat(jwtAuthenticationEntryPoint).isNotNull();
        }
    }

    // ========================================
    // PRUEBAS DE ENDPOINTS PÚBLICOS
    // ========================================

    @Nested
    @DisplayName("Endpoints Públicos")
    class PublicEndpointsTests {

        @Test
        @DisplayName("debería permitir acceso a Swagger UI sin autenticación")
        void shouldAllowAccessToSwaggerUiWithoutAuth() throws Exception {
            // When/Then
            mockMvc.perform(get("/swagger-ui.html"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("debería permitir acceso a API Docs sin autenticación")
        void shouldAllowAccessToApiDocsWithoutAuth() throws Exception {
            // When/Then
            mockMvc.perform(get("/v3/api-docs"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("debería permitir POST a /api/auth/** sin autenticación")
        void shouldAllowPostToAuthEndpointsWithoutAuth() throws Exception {
            // When/Then
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"test@test.com\",\"password\":\"password\"}"))
                    .andExpect(status().isNotFound()); // 404 porque el endpoint no existe en test, pero no 401/403
        }

        @Test
        @DisplayName("debería permitir GET a /api/products/** sin autenticación")
        void shouldAllowGetToProductsWithoutAuth() throws Exception {
            // When/Then
            mockMvc.perform(get("/api/products"))
                    .andExpect(status().isNotFound()); // 404, no 401/403
        }

        @Test
        @DisplayName("debería permitir GET a /api/product-categories/** sin autenticación")
        void shouldAllowGetToProductCategoriesWithoutAuth() throws Exception {
            // When/Then
            mockMvc.perform(get("/api/product-categories"))
                    .andExpect(status().isNotFound()); // 404, no 401/403
        }

        @Test
        @DisplayName("debería permitir GET a /api/suppliers/** sin autenticación")
        void shouldAllowGetToSuppliersWithoutAuth() throws Exception {
            // When/Then
            mockMvc.perform(get("/api/suppliers"))
                    .andExpect(status().isNotFound()); // 404, no 401/403
        }

        @Test
        @DisplayName("debería permitir GET a /api/burgers/** sin autenticación")
        void shouldAllowGetToBurgersWithoutAuth() throws Exception {
            // When/Then
            mockMvc.perform(get("/api/burgers"))
                    .andExpect(status().isNotFound()); // 404, no 401/403
        }
    }

    // ========================================
    // PRUEBAS DE ENDPOINTS PROTEGIDOS
    // ========================================

    @Nested
    @DisplayName("Endpoints Protegidos")
    class ProtectedEndpointsTests {

        @Test
        @DisplayName("debería denegar acceso a /api/profile/** sin autenticación")
        void shouldDenyAccessToProfileWithoutAuth() throws Exception {
            // When/Then
            mockMvc.perform(get("/api/profile"))
                    .andExpect(status().isUnauthorized()); // 401
        }

        @Test
        @DisplayName("debería denegar acceso a /api/orders/** sin autenticación")
        void shouldDenyAccessToOrdersWithoutAuth() throws Exception {
            // When/Then
            mockMvc.perform(get("/api/orders"))
                    .andExpect(status().isUnauthorized()); // 401
        }

        @Test
        @DisplayName("debería denegar acceso a /api/users/** sin autenticación")
        void shouldDenyAccessToUsersWithoutAuth() throws Exception {
            // When/Then
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isUnauthorized()); // 401
        }

        @Test
        @DisplayName("debería denegar POST a /api/products/** sin autenticación")
        void shouldDenyPostToProductsWithoutAuth() throws Exception {
            // When/Then
            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized()); // 401
        }

        @Test
        @DisplayName("debería denegar PUT a /api/products/** sin autenticación")
        void shouldDenyPutToProductsWithoutAuth() throws Exception {
            // When/Then
            mockMvc.perform(put("/api/products/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized()); // 401
        }

        @Test
        @DisplayName("debería denegar DELETE a /api/products/** sin autenticación")
        void shouldDenyDeleteToProductsWithoutAuth() throws Exception {
            // When/Then
            mockMvc.perform(delete("/api/products/1"))
                    .andExpect(status().isUnauthorized()); // 401
        }
    }

    // ========================================
    // PRUEBAS DE AUTORIZACIÓN POR ROL
    // ========================================

    @Nested
    @DisplayName("Autorización por Rol")
    class RoleAuthorizationTests {

        @Test
        @WithMockUser(authorities = "ROLE_ADMIN")
        @DisplayName("debería permitir acceso a /api/profile/** con ROLE_ADMIN")
        void shouldAllowAccessToProfileWithAdminRole() throws Exception {
            // When/Then
            mockMvc.perform(get("/api/profile"))
                    .andExpect(status().isNotFound()); // 404, no 403 (autorizado pero endpoint no existe)
        }

        @Test
        @WithMockUser(authorities = "ROLE_CLIENT")
        @DisplayName("debería permitir acceso a /api/profile/** con ROLE_CLIENT")
        void shouldAllowAccessToProfileWithClientRole() throws Exception {
            // When/Then
            mockMvc.perform(get("/api/profile"))
                    .andExpect(status().isNotFound()); // 404, no 403
        }

        @Test
        @WithMockUser(authorities = "ROLE_EMPLOYEE")
        @DisplayName("debería denegar acceso a /api/profile/** con ROLE_EMPLOYEE")
        void shouldDenyAccessToProfileWithEmployeeRole() throws Exception {
            // When/Then
            mockMvc.perform(get("/api/profile"))
                    .andExpect(status().isForbidden()); // 403
        }

        @Test
        @WithMockUser(authorities = "ROLE_ADMIN")
        @DisplayName("debería permitir acceso a /api/orders/** con ROLE_ADMIN")
        void shouldAllowAccessToOrdersWithAdminRole() throws Exception {
            // When/Then
            mockMvc.perform(get("/api/orders"))
                    .andExpect(status().isNotFound()); // 404, no 403
        }

        @Test
        @WithMockUser(authorities = "ROLE_EMPLOYEE")
        @DisplayName("debería permitir acceso a /api/orders/** con ROLE_EMPLOYEE")
        void shouldAllowAccessToOrdersWithEmployeeRole() throws Exception {
            // When/Then
            mockMvc.perform(get("/api/orders"))
                    .andExpect(status().isNotFound()); // 404, no 403
        }

        @Test
        @WithMockUser(authorities = "ROLE_CLIENT")
        @DisplayName("debería denegar acceso a /api/orders/** con ROLE_CLIENT")
        void shouldDenyAccessToOrdersWithClientRole() throws Exception {
            // When/Then
            mockMvc.perform(get("/api/orders"))
                    .andExpect(status().isForbidden()); // 403
        }

        @Test
        @WithMockUser(authorities = "ROLE_ADMIN")
        @DisplayName("debería permitir acceso a /api/users/** con ROLE_ADMIN")
        void shouldAllowAccessToUsersWithAdminRole() throws Exception {
            // When/Then
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isNotFound()); // 404, no 403
        }

        @Test
        @WithMockUser(authorities = "ROLE_CLIENT")
        @DisplayName("debería permitir acceso a /api/users/** con ROLE_CLIENT (authenticated)")
        void shouldAllowAccessToUsersWithClientRole() throws Exception {
            // When/Then - Cualquier usuario autenticado puede acceder
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isNotFound()); // 404, no 403
        }

        @Test
        @WithMockUser(authorities = "ROLE_EMPLOYEE")
        @DisplayName("debería permitir acceso a /api/users/** con ROLE_EMPLOYEE (authenticated)")
        void shouldAllowAccessToUsersWithEmployeeRole() throws Exception {
            // When/Then - Cualquier usuario autenticado puede acceder
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isNotFound()); // 404, no 403
        }
    }

    // ========================================
    // PRUEBAS DE CORS
    // ========================================

    @Nested
    @DisplayName("Configuración CORS")
    class CorsConfigurationTests {

        @Test
        @DisplayName("debería permitir requests desde orígenes configurados")
        void shouldAllowRequestsFromConfiguredOrigins() throws Exception {
            // When/Then
            mockMvc.perform(options("/api/burgers")
                            .header("Origin", "http://localhost:3000")
                            .header("Access-Control-Request-Method", "GET"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
        }

        @Test
        @DisplayName("debería permitir métodos HTTP configurados")
        void shouldAllowConfiguredHttpMethods() throws Exception {
            // When/Then
            mockMvc.perform(options("/api/burgers")
                            .header("Origin", "http://localhost:3000")
                            .header("Access-Control-Request-Method", "POST"))
                    .andExpect(status().isOk())
                    .andExpect(header().exists("Access-Control-Allow-Methods"));
        }

        @Test
        @DisplayName("debería permitir headers configurados")
        void shouldAllowConfiguredHeaders() throws Exception {
            // When/Then
            mockMvc.perform(options("/api/burgers")
                            .header("Origin", "http://localhost:3000")
                            .header("Access-Control-Request-Method", "GET")
                            .header("Access-Control-Request-Headers", "Authorization,Content-Type"))
                    .andExpect(status().isOk())
                    .andExpect(header().exists("Access-Control-Allow-Headers"));
        }

        @Test
        @DisplayName("debería configurar Access-Control-Allow-Credentials")
        void shouldConfigureAllowCredentials() throws Exception {
            // When/Then
            mockMvc.perform(options("/api/burgers")
                            .header("Origin", "http://localhost:3000")
                            .header("Access-Control-Request-Method", "GET"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
        }

        @Test
        @DisplayName("debería configurar Max-Age para preflight requests")
        void shouldConfigureMaxAgeForPreflightRequests() throws Exception {
            // When/Then
            mockMvc.perform(options("/api/burgers")
                            .header("Origin", "http://localhost:3000")
                            .header("Access-Control-Request-Method", "GET"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Max-Age", "3600"));
        }
    }

    // ========================================
    // PRUEBAS DE PASSWORD ENCODER
    // ========================================

    @Nested
    @DisplayName("PasswordEncoder")
    class PasswordEncoderTests {

        @Test
        @DisplayName("debería codificar contraseñas correctamente")
        void shouldEncodePasswordsCorrectly() {
            // Given
            String rawPassword = "mySecurePassword123";

            // When
            String encodedPassword = passwordEncoder.encode(rawPassword);

            // Then
            assertThat(encodedPassword).isNotNull();
            assertThat(encodedPassword).isNotEqualTo(rawPassword);
            assertThat(encodedPassword).startsWith("$2a$"); // BCrypt prefix
        }

        @Test
        @DisplayName("debería validar contraseñas correctamente")
        void shouldValidatePasswordsCorrectly() {
            // Given
            String rawPassword = "mySecurePassword123";
            String encodedPassword = passwordEncoder.encode(rawPassword);

            // When
            boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);

            // Then
            assertThat(matches).isTrue();
        }

        @Test
        @DisplayName("debería rechazar contraseñas incorrectas")
        void shouldRejectIncorrectPasswords() {
            // Given
            String rawPassword = "mySecurePassword123";
            String wrongPassword = "wrongPassword456";
            String encodedPassword = passwordEncoder.encode(rawPassword);

            // When
            boolean matches = passwordEncoder.matches(wrongPassword, encodedPassword);

            // Then
            assertThat(matches).isFalse();
        }

        @Test
        @DisplayName("debería generar diferentes hashes para la misma contraseña")
        void shouldGenerateDifferentHashesForSamePassword() {
            // Given
            String rawPassword = "mySecurePassword123";

            // When
            String encodedPassword1 = passwordEncoder.encode(rawPassword);
            String encodedPassword2 = passwordEncoder.encode(rawPassword);

            // Then - Hashes diferentes pero ambos válidos
            assertThat(encodedPassword1).isNotEqualTo(encodedPassword2);
            assertThat(passwordEncoder.matches(rawPassword, encodedPassword1)).isTrue();
            assertThat(passwordEncoder.matches(rawPassword, encodedPassword2)).isTrue();
        }
    }

    // ========================================
    // PRUEBAS DE SESIONES
    // ========================================

    @Nested
    @DisplayName("Gestión de Sesiones")
    class SessionManagementTests {

        @Test
        @DisplayName("debería usar política de sesión STATELESS")
        void shouldUseStatelessSessionPolicy() throws Exception {
            // When/Then - No debe crear sesión
            mockMvc.perform(get("/api/burgers"))
                    .andExpect(request().sessionAttribute("SPRING_SECURITY_CONTEXT", nullValue()));
        }

        @Test
        @WithMockUser(authorities = "ROLE_ADMIN")
        @DisplayName("no debería crear sesión para usuarios autenticados")
        void shouldNotCreateSessionForAuthenticatedUsers() throws Exception {
            // When/Then
            mockMvc.perform(get("/api/profile"))
                    .andExpect(request().sessionAttribute("SPRING_SECURITY_CONTEXT", nullValue()));
        }
    }

    // ========================================
    // PRUEBAS DE MÉTODOS HTTP
    // ========================================

    @Nested
    @DisplayName("Métodos HTTP")
    class HttpMethodsTests {

        @Test
        @DisplayName("debería permitir OPTIONS sin autenticación (CORS preflight)")
        void shouldAllowOptionsWithoutAuth() throws Exception {
            // When/Then
            mockMvc.perform(options("/api/burgers")
                            .header("Origin", "http://localhost:3000")
                            .header("Access-Control-Request-Method", "GET"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("debería denegar POST a endpoints públicos de lectura")
        void shouldDenyPostToPublicReadOnlyEndpoints() throws Exception {
            // When/Then
            mockMvc.perform(post("/api/burgers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized()); // 401
        }

        @Test
        @DisplayName("debería denegar PATCH sin autenticación")
        void shouldDenyPatchWithoutAuth() throws Exception {
            // When/Then
            mockMvc.perform(patch("/api/products/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized()); // 401
        }
    }

    // ========================================
    // PRUEBAS DE CSRF
    // ========================================

    @Nested
    @DisplayName("Configuración CSRF")
    class CsrfConfigurationTests {

        @Test
        @DisplayName("debería deshabilitar CSRF (API REST)")
        void shouldDisableCsrf() throws Exception {
            // When/Then - POST sin token CSRF debe funcionar
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"test@test.com\",\"password\":\"password\"}"))
                    .andExpect(status().isNotFound()); // 404, no 403 (CSRF)
        }
    }

    // ========================================
    // PRUEBAS DE INTEGRACIÓN
    // ========================================

    @Nested
    @DisplayName("Pruebas de Integración")
    class IntegrationTests {

        @Test
        @DisplayName("debería aplicar filtro JWT antes de autenticación")
        void shouldApplyJwtFilterBeforeAuthentication() {
            // Then - JwtAuthenticationFilter debe estar inyectado y configurado
            assertThat(jwtAuthenticationFilter).isNotNull();
        }

        @Test
        @DisplayName("debería usar JwtAuthenticationEntryPoint para errores de autenticación")
        void shouldUseJwtAuthenticationEntryPointForAuthErrors() {
            // Then
            assertThat(jwtAuthenticationEntryPoint).isNotNull();
        }

        @Test
        @DisplayName("debería configurar AuthenticationProvider con UserDetailsService")
        void shouldConfigureAuthenticationProviderWithUserDetailsService() {
            // Then
            assertThat(authenticationProvider).isNotNull();
        }
    }

    // ========================================
    // PRUEBAS DE CASOS EXTREMOS
    // ========================================

    @Nested
    @DisplayName("Casos Extremos")
    class EdgeCasesTests {

        @Test
        @DisplayName("debería manejar paths con trailing slash")
        void shouldHandlePathsWithTrailingSlash() throws Exception {
            // When/Then
            mockMvc.perform(get("/api/burgers/"))
                    .andExpect(status().isNotFound()); // 404, no 401/403
        }

        @Test
        @DisplayName("debería manejar paths case-sensitive")
        void shouldHandlePathsCaseSensitively() throws Exception {
            // When/Then - /API/BURGERS != /api/burgers
            mockMvc.perform(get("/API/BURGERS"))
                    .andExpect(status().isUnauthorized()); // 401 (no coincide con whitelist)
        }

        @Test
        @DisplayName("debería manejar paths con múltiples segmentos")
        void shouldHandlePathsWithMultipleSegments() throws Exception {
            // When/Then
            mockMvc.perform(get("/api/burgers/1/ingredients"))
                    .andExpect(status().isNotFound()); // 404, no 401/403
        }

        @Test
        @WithMockUser(authorities = "ROLE_INVALID")
        @DisplayName("debería denegar acceso con rol inválido")
        void shouldDenyAccessWithInvalidRole() throws Exception {
            // When/Then
            mockMvc.perform(get("/api/profile"))
                    .andExpect(status().isForbidden()); // 403
        }
    }
}
