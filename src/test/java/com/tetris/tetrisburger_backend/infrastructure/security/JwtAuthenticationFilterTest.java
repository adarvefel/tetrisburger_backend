package com.tetris.tetrisburger_backend.infrastructure.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        userDetails = User.builder()
                .username("test@example.com")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT")))
                .build();
    }

    @Nested
    @DisplayName("Pruebas de Extracción de Token")
    class TokenExtractionTests {

        @Test
        @DisplayName("Debería procesar solicitud sin token en el header")
        void shouldProcessRequestWithoutToken() throws ServletException, IOException {
            // Given
            when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            verify(filterChain).doFilter(request, response);
            verify(jwtUtil, never()).extractUsername(anyString());
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }

        @Test
        @DisplayName("Debería procesar solicitud con header de autorización vacío")
        void shouldProcessRequestWithEmptyAuthHeader() throws ServletException, IOException {
            // Given
            when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("");

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            verify(filterChain).doFilter(request, response);
            verify(jwtUtil, never()).extractUsername(anyString());
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }

        @Test
        @DisplayName("Debería procesar solicitud con header sin prefijo Bearer")
        void shouldProcessRequestWithoutBearerPrefix() throws ServletException, IOException {
            // Given
            when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("InvalidToken");

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            verify(filterChain).doFilter(request, response);
            verify(jwtUtil, never()).extractUsername(anyString());
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }

        @Test
        @DisplayName("Debería extraer token correctamente del header Bearer")
        void shouldExtractTokenFromBearerHeader() throws ServletException, IOException {
            // Given
            String token = "valid.jwt.token";
            when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
            when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
            when(jwtUtil.validateToken(token, userDetails)).thenReturn(true);

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            verify(jwtUtil).extractUsername(token);
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("Pruebas de Autenticación Exitosa")
    class SuccessfulAuthenticationTests {

        @Test
        @DisplayName("Debería autenticar usuario con token válido")
        void shouldAuthenticateUserWithValidToken() throws ServletException, IOException {
            // Given
            String token = "valid.jwt.token";
            when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
            when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
            when(jwtUtil.validateToken(token, userDetails)).thenReturn(true);

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
            assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(userDetails);
            assertThat(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()).isTrue();
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Debería establecer authorities correctamente en autenticación")
        void shouldSetAuthoritiesCorrectlyInAuthentication() throws ServletException, IOException {
            // Given
            String token = "valid.jwt.token";
            when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
            when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
            when(jwtUtil.validateToken(token, userDetails)).thenReturn(true);

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                    .hasSize(1)
                    .extracting("authority")
                    .contains("ROLE_CLIENT");
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Debería establecer detalles de autenticación web")
        void shouldSetWebAuthenticationDetails() throws ServletException, IOException {
            // Given
            String token = "valid.jwt.token";
            when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
            when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
            when(jwtUtil.validateToken(token, userDetails)).thenReturn(true);

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            assertThat(SecurityContextHolder.getContext().getAuthentication().getDetails()).isNotNull();
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("Pruebas de Validación de Token")
    class TokenValidationTests {

        @Test
        @DisplayName("No debería autenticar cuando token es inválido")
        void shouldNotAuthenticateWhenTokenIsInvalid() throws ServletException, IOException {
            // Given
            String token = "invalid.jwt.token";
            when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
            when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
            when(jwtUtil.validateToken(token, userDetails)).thenReturn(false);

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Debería manejar token JWT expirado")
        void shouldHandleExpiredJwtToken() throws ServletException, IOException {
            // Given
            String token = "expired.jwt.token";
            when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
            verify(userDetailsService, never()).loadUserByUsername(anyString());
        }

        @Test
        @DisplayName("Debería manejar excepción JWT genérica")
        void shouldHandleGenericJwtException() throws ServletException, IOException {
            // Given
            String token = "malformed.jwt.token";
            when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenThrow(new JwtException("Malformed token"));

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
            verify(userDetailsService, never()).loadUserByUsername(anyString());
        }

        @Test
        @DisplayName("Debería manejar excepción general durante procesamiento")
        void shouldHandleGeneralExceptionDuringProcessing() throws ServletException, IOException {
            // Given
            String token = "valid.jwt.token";
            when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenThrow(new RuntimeException("Unexpected error"));

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("Pruebas de Contexto de Seguridad")
    class SecurityContextTests {

        @Test
        @DisplayName("No debería sobrescribir autenticación existente")
        void shouldNotOverrideExistingAuthentication() throws ServletException, IOException {
            // Given
            String token = "valid.jwt.token";
            UserDetails existingUser = User.builder()
                    .username("existing@example.com")
                    .password("password")
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                    .build();

            org.springframework.security.authentication.UsernamePasswordAuthenticationToken existingAuth =
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            existingUser, null, existingUser.getAuthorities()
                    );
            SecurityContextHolder.getContext().setAuthentication(existingAuth);

            when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(existingAuth);
            assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(existingUser);
            verify(userDetailsService, never()).loadUserByUsername(anyString());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("No debería autenticar cuando username es null")
        void shouldNotAuthenticateWhenUsernameIsNull() throws ServletException, IOException {
            // Given
            String token = "valid.jwt.token";
            when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn(null);

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(userDetailsService, never()).loadUserByUsername(anyString());
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("Pruebas de Filtrado de Rutas")
    class RouteFilteringTests {

        @Test
        @DisplayName("No debería filtrar rutas de autenticación")
        void shouldNotFilterAuthenticationRoutes() throws ServletException {
            // Given
            when(request.getServletPath()).thenReturn("/api/auth/login");

            // When
            boolean shouldNotFilter = jwtAuthenticationFilter.shouldNotFilter(request);

            // Then
            assertThat(shouldNotFilter).isTrue();
        }

        @Test
        @DisplayName("No debería filtrar rutas de registro")
        void shouldNotFilterRegistrationRoutes() throws ServletException {
            // Given
            when(request.getServletPath()).thenReturn("/api/auth/register");

            // When
            boolean shouldNotFilter = jwtAuthenticationFilter.shouldNotFilter(request);

            // Then
            assertThat(shouldNotFilter).isTrue();
        }

        @Test
        @DisplayName("Debería filtrar rutas protegidas")
        void shouldFilterProtectedRoutes() throws ServletException {
            // Given
            when(request.getServletPath()).thenReturn("/api/users/profile");

            // When
            boolean shouldNotFilter = jwtAuthenticationFilter.shouldNotFilter(request);

            // Then
            assertThat(shouldNotFilter).isFalse();
        }

        @Test
        @DisplayName("Debería filtrar rutas de admin")
        void shouldFilterAdminRoutes() throws ServletException {
            // Given
            when(request.getServletPath()).thenReturn("/api/admin/users");

            // When
            boolean shouldNotFilter = jwtAuthenticationFilter.shouldNotFilter(request);

            // Then
            assertThat(shouldNotFilter).isFalse();
        }
    }

    @Nested
    @DisplayName("Pruebas de Carga de Usuario")
    class UserLoadingTests {

        @Test
        @DisplayName("Debería cargar usuario desde UserDetailsService")
        void shouldLoadUserFromUserDetailsService() throws ServletException, IOException {
            // Given
            String token = "valid.jwt.token";
            String username = "test@example.com";
            when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn(username);
            when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
            when(jwtUtil.validateToken(token, userDetails)).thenReturn(true);

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            verify(userDetailsService).loadUserByUsername(username);
            verify(jwtUtil).validateToken(token, userDetails);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("No debería autenticar cuando falla la carga de usuario")
        void shouldNotAuthenticateWhenUserLoadingFails() throws ServletException, IOException {
            // Given
            String token = "valid.jwt.token";
            when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
            when(userDetailsService.loadUserByUsername("test@example.com"))
                    .thenThrow(new RuntimeException("User not found"));

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("Pruebas de Cadena de Filtros")
    class FilterChainTests {

        @Test
        @DisplayName("Debería continuar cadena de filtros con autenticación exitosa")
        void shouldContinueFilterChainWithSuccessfulAuth() throws ServletException, IOException {
            // Given
            String token = "valid.jwt.token";
            when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
            when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
            when(jwtUtil.validateToken(token, userDetails)).thenReturn(true);

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Debería continuar cadena de filtros con autenticación fallida")
        void shouldContinueFilterChainWithFailedAuth() throws ServletException, IOException {
            // Given
            String token = "invalid.jwt.token";
            when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenThrow(new JwtException("Invalid token"));

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Debería continuar cadena de filtros sin token")
        void shouldContinueFilterChainWithoutToken() throws ServletException, IOException {
            // Given
            when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("Pruebas de Tokens con Espacios")
    class TokenWithSpacesTests {

        @Test
        @DisplayName("Debería manejar token Bearer con espacios extra")
        void shouldHandleBearerTokenWithExtraSpaces() throws ServletException, IOException {
            // Given
            when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer  token.with.spaces");

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Debería extraer token correctamente después de Bearer")
        void shouldExtractTokenCorrectlyAfterBearer() throws ServletException, IOException {
            // Given
            String token = "valid.jwt.token";
            when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
            when(jwtUtil.extractUsername(token)).thenReturn("test@example.com");
            when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
            when(jwtUtil.validateToken(token, userDetails)).thenReturn(true);

            // When
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Then
            verify(jwtUtil).extractUsername(token);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
            verify(filterChain).doFilter(request, response);
        }
    }
}
