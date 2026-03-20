package com.tetris.tetrisburger_backend.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de JwtAuthenticationFilter")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private ObjectMapper objectMapper; // ← dependencia faltante en la versión anterior

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserDetails userDetails;

    // Para los tests de excepción usamos Mocks reales de servlet
    // Para los tests de error response usamos MockHttpServletRequest/Response de Spring
    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        userDetails = User.builder()
                .username("test@example.com")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT")))
                .build();
    }

    // ─────────────────────────────────────────────────────────────
    // Extracción de token
    // ─────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Pruebas de extracción de token")
    class TokenExtractionTests {

        @Test
        @DisplayName("Debería continuar la cadena sin procesar token cuando el header Authorization es nulo")
        void shouldContinueChainWithoutProcessingWhenAuthHeaderIsNull()
                throws ServletException, IOException {
            when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

            jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, filterChain);

            verify(filterChain).doFilter(mockRequest, mockResponse);
            verify(jwtUtil, never()).extractUsername(anyString());
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }

        @Test
        @DisplayName("Debería continuar la cadena sin procesar cuando el header Authorization está vacío")
        void shouldContinueChainWhenAuthHeaderIsEmpty()
                throws ServletException, IOException {
            when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("");

            jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, filterChain);

            verify(filterChain).doFilter(mockRequest, mockResponse);
            verify(jwtUtil, never()).extractUsername(anyString());
        }

        @Test
        @DisplayName("Debería continuar la cadena sin procesar cuando el header no tiene prefijo Bearer")
        void shouldContinueChainWhenHeaderHasNoBearerPrefix()
                throws ServletException, IOException {
            when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic dXNlcjpwYXNz");

            jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, filterChain);

            verify(filterChain).doFilter(mockRequest, mockResponse);
            verify(jwtUtil, never()).extractUsername(anyString());
        }

        @Test
        @DisplayName("Debería extraer y pasar el token correctamente al JwtUtil cuando el header es válido")
        void shouldExtractTokenAndPassToJwtUtilWhenHeaderIsValid()
                throws ServletException, IOException {
            String token = "valid.jwt.token";
            when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
            when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
            when(jwtUtil.validateToken(token, userDetails)).thenReturn(true);

            jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, filterChain);

            verify(jwtUtil).extractUsername(token);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Autenticación exitosa
    // ─────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Pruebas de autenticación exitosa")
    class SuccessfulAuthenticationTests {

        @Test
        @DisplayName("Debería autenticar al usuario y poblarlo en el SecurityContext con token válido")
        void shouldAuthenticateUserAndSetSecurityContextWithValidToken()
                throws ServletException, IOException {
            String token = "valid.jwt.token";
            when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
            when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
            when(jwtUtil.validateToken(token, userDetails)).thenReturn(true);

            jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
            assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                    .isEqualTo(userDetails);
            assertThat(SecurityContextHolder.getContext().getAuthentication().isAuthenticated())
                    .isTrue();
            verify(filterChain).doFilter(mockRequest, mockResponse);
        }

        @Test
        @DisplayName("Debería establecer las authorities correctamente en el contexto de seguridad")
        void shouldSetAuthoritiesCorrectlyInSecurityContext()
                throws ServletException, IOException {
            String token = "valid.jwt.token";
            when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
            when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
            when(jwtUtil.validateToken(token, userDetails)).thenReturn(true);

            jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                    .hasSize(1)
                    .extracting("authority")
                    .contains("ROLE_CLIENT");
        }

        @Test
        @DisplayName("Debería establecer los detalles de autenticación web (WebAuthenticationDetails)")
        void shouldSetWebAuthenticationDetails()
                throws ServletException, IOException {
            String token = "valid.jwt.token";
            when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
            when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
            when(jwtUtil.validateToken(token, userDetails)).thenReturn(true);

            jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication().getDetails())
                    .isNotNull();
        }

        @Test
        @DisplayName("No debería autenticar cuando el token no supera la validación")
        void shouldNotAuthenticateWhenTokenValidationFails()
                throws ServletException, IOException {
            String token = "invalid.jwt.token";
            when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
            when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
            when(jwtUtil.validateToken(token, userDetails)).thenReturn(false);

            jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(mockRequest, mockResponse);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Manejo de excepciones JWT
    // El filtro real escribe la respuesta de error y hace return —
    // NO llama a filterChain.doFilter() en estos casos
    // ─────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Pruebas de manejo de excepciones JWT")
    class JwtExceptionHandlingTests {

        // Usamos MockHttpServletResponse real para verificar el body escrito
        private MockHttpServletRequest realRequest;
        private MockHttpServletResponse realResponse;
        private ObjectMapper realObjectMapper;
        private JwtAuthenticationFilter filterWithRealMapper;

        @BeforeEach
        void setUpRealFilter() {
            realRequest  = new MockHttpServletRequest();
            realResponse = new MockHttpServletResponse();
            realObjectMapper = new ObjectMapper();
            realObjectMapper.findAndRegisterModules();
            // Instanciar con mapper real para verificar el body escrito
            filterWithRealMapper = new JwtAuthenticationFilter(jwtUtil, userDetailsService, realObjectMapper);
        }

        @Test
        @DisplayName("Debería escribir respuesta 401 y NO continuar la cadena cuando el token está expirado")
        void shouldWrite401AndNotContinueChainForExpiredToken()
                throws ServletException, IOException {
            String token = "expired.jwt.token";
            realRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            when(jwtUtil.extractUsername(token))
                    .thenThrow(new ExpiredJwtException(null, null, "Token expired"));

            filterWithRealMapper.doFilterInternal(realRequest, realResponse, filterChain);

            assertThat(realResponse.getStatus()).isEqualTo(401);
            assertThat(realResponse.getContentAsString()).contains("Token expirado");
            verify(filterChain, never()).doFilter(any(), any()); // ← NO llama a doFilter
        }

        @Test
        @DisplayName("Debería escribir respuesta 401 y NO continuar la cadena cuando el token está malformado")
        void shouldWrite401AndNotContinueChainForMalformedToken()
                throws ServletException, IOException {
            String token = "malformed.jwt.token";
            realRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            when(jwtUtil.extractUsername(token))
                    .thenThrow(new MalformedJwtException("Malformed"));

            filterWithRealMapper.doFilterInternal(realRequest, realResponse, filterChain);

            assertThat(realResponse.getStatus()).isEqualTo(401);
            assertThat(realResponse.getContentAsString()).contains("Token malformado");
            verify(filterChain, never()).doFilter(any(), any());
        }

        @Test
        @DisplayName("Debería escribir respuesta 401 y NO continuar la cadena cuando la firma es inválida")
        void shouldWrite401AndNotContinueChainForInvalidSignature()
                throws ServletException, IOException {
            String token = "bad.signature.token";
            realRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            when(jwtUtil.extractUsername(token))
                    .thenThrow(new SignatureException("Bad signature"));

            filterWithRealMapper.doFilterInternal(realRequest, realResponse, filterChain);

            assertThat(realResponse.getStatus()).isEqualTo(401);
            assertThat(realResponse.getContentAsString()).contains("Firma inválida");
            verify(filterChain, never()).doFilter(any(), any());
        }

        @Test
        @DisplayName("Debería escribir respuesta 401 y NO continuar la cadena cuando el token no es soportado")
        void shouldWrite401AndNotContinueChainForUnsupportedToken()
                throws ServletException, IOException {
            String token = "unsupported.jwt.token";
            realRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            when(jwtUtil.extractUsername(token))
                    .thenThrow(new UnsupportedJwtException("Unsupported"));

            filterWithRealMapper.doFilterInternal(realRequest, realResponse, filterChain);

            assertThat(realResponse.getStatus()).isEqualTo(401);
            assertThat(realResponse.getContentAsString()).contains("Token no soportado");
            verify(filterChain, never()).doFilter(any(), any());
        }

        @Test
        @DisplayName("Debería escribir respuesta 401 y NO continuar la cadena ante excepción genérica")
        void shouldWrite401AndNotContinueChainForGenericException()
                throws ServletException, IOException {
            String token = "valid.jwt.token";
            realRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            when(jwtUtil.extractUsername(token))
                    .thenThrow(new RuntimeException("Unexpected error"));

            filterWithRealMapper.doFilterInternal(realRequest, realResponse, filterChain);

            assertThat(realResponse.getStatus()).isEqualTo(401);
            assertThat(realResponse.getContentAsString()).contains("Error de autenticación");
            verify(filterChain, never()).doFilter(any(), any());
        }

        @Test
        @DisplayName("Debería escribir respuesta 401 cuando el argumento del token es ilegal")
        void shouldWrite401ForIllegalArgumentException()
                throws ServletException, IOException {
            String token = "valid.jwt.token";
            realRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            when(jwtUtil.extractUsername(token))
                    .thenThrow(new IllegalArgumentException("Null token"));

            filterWithRealMapper.doFilterInternal(realRequest, realResponse, filterChain);

            assertThat(realResponse.getStatus()).isEqualTo(401);
            assertThat(realResponse.getContentAsString()).contains("Token inválido");
            verify(filterChain, never()).doFilter(any(), any());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Contexto de seguridad
    // ─────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Pruebas de contexto de seguridad")
    class SecurityContextTests {

        @Test
        @DisplayName("No debería sobrescribir una autenticación ya existente en el contexto")
        void shouldNotOverwriteExistingAuthentication()
                throws ServletException, IOException {
            org.springframework.security.authentication.UsernamePasswordAuthenticationToken existingAuth =
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
            SecurityContextHolder.getContext().setAuthentication(existingAuth);

            String token = "valid.jwt.token";
            when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
            // extractUsername devuelve el mismo usuario que ya está autenticado →
            // el filtro no llama a loadUserByUsername porque auth != null

            jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(existingAuth);
            verify(userDetailsService, never()).loadUserByUsername(anyString());
            verify(filterChain).doFilter(mockRequest, mockResponse);
        }

        @Test
        @DisplayName("No debería autenticar cuando el username extraído del token es nulo")
        void shouldNotAuthenticateWhenUsernameIsNull()
                throws ServletException, IOException {
            String token = "valid.jwt.token";
            when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn(null);

            jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(userDetailsService, never()).loadUserByUsername(anyString());
            verify(filterChain).doFilter(mockRequest, mockResponse);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // shouldNotFilter
    // ─────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Pruebas de shouldNotFilter")
    class ShouldNotFilterTests {

        @Test
        @DisplayName("Debería excluir del filtro cualquier ruta que empiece con /api/auth/")
        void shouldExcludeAnyPathStartingWithApiAuth() throws ServletException {
            when(mockRequest.getServletPath()).thenReturn("/api/auth/login");
            assertThat(jwtAuthenticationFilter.shouldNotFilter(mockRequest)).isTrue();
        }

        @Test
        @DisplayName("Debería excluir del filtro la ruta de registro")
        void shouldExcludeRegistrationPath() throws ServletException {
            when(mockRequest.getServletPath()).thenReturn("/api/auth/register");
            assertThat(jwtAuthenticationFilter.shouldNotFilter(mockRequest)).isTrue();
        }

        @Test
        @DisplayName("Debería excluir del filtro cualquier subruta de /api/auth/")
        void shouldExcludeAnySubpathUnderApiAuth() throws ServletException {
            when(mockRequest.getServletPath()).thenReturn("/api/auth/refresh-token");
            assertThat(jwtAuthenticationFilter.shouldNotFilter(mockRequest)).isTrue();
        }

        @Test
        @DisplayName("Debería aplicar el filtro a rutas protegidas de usuarios")
        void shouldApplyFilterToProtectedUserRoutes() throws ServletException {
            when(mockRequest.getServletPath()).thenReturn("/api/users/profile");
            assertThat(jwtAuthenticationFilter.shouldNotFilter(mockRequest)).isFalse();
        }

        @Test
        @DisplayName("Debería aplicar el filtro a rutas de administración")
        void shouldApplyFilterToAdminRoutes() throws ServletException {
            when(mockRequest.getServletPath()).thenReturn("/api/admin/users");
            assertThat(jwtAuthenticationFilter.shouldNotFilter(mockRequest)).isFalse();
        }

        @Test
        @DisplayName("No debería excluir una ruta que contiene /api/auth/ pero no empieza con ella")
        void shouldNotExcludePathThatContainsButNotStartsWithApiAuth() throws ServletException {
            when(mockRequest.getServletPath()).thenReturn("/v2/api/auth/login");
            assertThat(jwtAuthenticationFilter.shouldNotFilter(mockRequest)).isFalse();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Carga de usuario
    // ─────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Pruebas de carga de usuario")
    class UserLoadingTests {

        @Test
        @DisplayName("Debería cargar el usuario desde UserDetailsService y validar el token")
        void shouldLoadUserFromUserDetailsServiceAndValidateToken()
                throws ServletException, IOException {
            String token = "valid.jwt.token";
            when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
            when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
            when(jwtUtil.validateToken(token, userDetails)).thenReturn(true);

            jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, filterChain);

            verify(userDetailsService).loadUserByUsername("test@example.com");
            verify(jwtUtil).validateToken(token, userDetails);
        }

        @Test
        @DisplayName("Debería escribir respuesta 401 cuando falla la carga del usuario")
        void shouldWrite401WhenUserLoadingFails()
                throws ServletException, IOException {
            // Para este test necesitamos el mapper real para verificar el body
            MockHttpServletRequest realReq = new MockHttpServletRequest();
            MockHttpServletResponse realRes = new MockHttpServletResponse();
            ObjectMapper realMapper = new ObjectMapper();
            realMapper.findAndRegisterModules();
            JwtAuthenticationFilter filterWithRealMapper =
                    new JwtAuthenticationFilter(jwtUtil, userDetailsService, realMapper);

            String token = "valid.jwt.token";
            realReq.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
            when(userDetailsService.loadUserByUsername("test@example.com"))
                    .thenThrow(new RuntimeException("User not found"));

            filterWithRealMapper.doFilterInternal(realReq, realRes, filterChain);

            assertThat(realRes.getStatus()).isEqualTo(401);
            verify(filterChain, never()).doFilter(any(), any());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Cadena de filtros
    // ─────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Pruebas de continuidad de la cadena de filtros")
    class FilterChainContinuityTests {

        @Test
        @DisplayName("Debería continuar la cadena tras autenticación exitosa")
        void shouldContinueChainAfterSuccessfulAuth()
                throws ServletException, IOException {
            String token = "valid.jwt.token";
            when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
            when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
            when(jwtUtil.validateToken(token, userDetails)).thenReturn(true);

            jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, filterChain);

            verify(filterChain).doFilter(mockRequest, mockResponse);
        }

        @Test
        @DisplayName("Debería continuar la cadena cuando no se envía ningún token")
        void shouldContinueChainWhenNoToken()
                throws ServletException, IOException {
            when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

            jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, filterChain);

            verify(filterChain).doFilter(mockRequest, mockResponse);
        }

        @Test
        @DisplayName("Debería continuar la cadena cuando el token es válido pero la validación falla")
        void shouldContinueChainWhenTokenFailsValidation()
                throws ServletException, IOException {
            String token = "valid.jwt.token";
            when(mockRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
            when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
            when(jwtUtil.validateToken(token, userDetails)).thenReturn(false);

            jwtAuthenticationFilter.doFilterInternal(mockRequest, mockResponse, filterChain);

            verify(filterChain).doFilter(mockRequest, mockResponse);
        }
    }
}
