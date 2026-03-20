package com.tetris.tetrisburger_backend.infrastructure.security;

import com.tetris.tetrisburger_backend.domain.exception.InvalidTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de JwtUtil")
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @Mock
    private UserDetails userDetails;

    @Mock
    private CustomUserDetails customUserDetails;

    private static final String TEST_SECRET = "dGVzdFNlY3JldEtleUZvckpXVFRva2VuVGVzdGluZ1dpdGhTcHJpbmdCb290QW5kU2VjdXJpdHkxMjM0NTY=";
    private static final Long TEST_EXPIRATION = 3600000L;
    private static final Long SHORT_EXPIRATION = 1000L;
    private static final String TEST_EMAIL = "test@tetrisburger.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", TEST_EXPIRATION);
        lenient().when(userDetails.getUsername()).thenReturn(TEST_EMAIL);
    }

    // ── Generación de Tokens ──────────────────────────────────────────────

    @Nested
    @DisplayName("Generación de Tokens de Autenticación")
    class AuthTokenGenerationTests {

        @Test
        @DisplayName("debería generar token válido con UserDetails")
        void shouldGenerateValidTokenWithUserDetails() {
            String token = jwtUtil.generateToken(userDetails);

            assertNotNull(token);
            assertTrue(token.length() > 0);
            assertEquals(3, token.split("\\.").length,
                    "JWT debe tener 3 partes (header.payload.signature)");
        }

        @Test
        @DisplayName("debería generar token con estructura JWT válida")
        void shouldGenerateTokenWithValidJwtStructure() {
            String token = jwtUtil.generateToken(userDetails);
            String[] parts = token.split("\\.");

            assertTrue(parts[0].length() > 0, "Header no debe estar vacío");
            assertTrue(parts[1].length() > 0, "Payload no debe estar vacío");
            assertTrue(parts[2].length() > 0, "Signature no debe estar vacía");
        }

        @Test
        @DisplayName("debería generar tokens diferentes para llamadas consecutivas")
        void shouldGenerateDifferentTokensForConsecutiveCalls() throws InterruptedException {
            String token1 = jwtUtil.generateToken(userDetails);
            Thread.sleep(1100); // FIX: JWT usa precisión de segundos — mínimo 1000ms para diferente iat
            String token2 = jwtUtil.generateToken(userDetails);

            assertNotEquals(token1, token2, "Tokens deben ser diferentes por timestamp");
        }

        @Test
        @DisplayName("debería lanzar excepción con UserDetails nulo")
        void shouldThrowExceptionWithNullUserDetails() {
            assertThrows(NullPointerException.class,
                    () -> jwtUtil.generateToken(null));
        }

        @Test
        @DisplayName("debería generar token con username que contiene caracteres especiales")
        void shouldGenerateTokenWithSpecialCharactersInUsername() {
            when(userDetails.getUsername()).thenReturn("user+test@example.com");

            String token = jwtUtil.generateToken(userDetails);

            assertNotNull(token);
            assertEquals("user+test@example.com", jwtUtil.extractUsername(token));
        }
    }

    // ── Extracción de Username ────────────────────────────────────────────

    @Nested
    @DisplayName("Extracción de Username")
    class UsernameExtractionTests {

        @Test
        @DisplayName("debería extraer username correcto del token")
        void shouldExtractCorrectUsername() {
            String token = jwtUtil.generateToken(userDetails);

            assertEquals(TEST_EMAIL, jwtUtil.extractUsername(token));
        }

        @Test
        @DisplayName("debería lanzar MalformedJwtException con token malformado")
        void shouldThrowMalformedJwtExceptionWithMalformedToken() {
            assertThrows(MalformedJwtException.class,
                    () -> jwtUtil.extractUsername("invalid.token.structure"));
        }

        @Test
        @DisplayName("debería lanzar excepción con token vacío")
        void shouldThrowExceptionWithEmptyToken() {
            assertThrows(Exception.class,
                    () -> jwtUtil.extractUsername(""));
        }

        @Test
        @DisplayName("debería lanzar excepción con token null")
        void shouldThrowExceptionWithNullToken() {
            assertThrows(Exception.class,
                    () -> jwtUtil.extractUsername(null));
        }

        @Test
        @DisplayName("debería lanzar SignatureException con token con firma alterada")
        void shouldThrowSignatureExceptionWithTamperedToken() {
            String token = jwtUtil.generateToken(userDetails);
            String tamperedToken = token.substring(0, token.length() - 10) + "XXXXXXXXXX";

            assertThrows(SignatureException.class,
                    () -> jwtUtil.extractUsername(tamperedToken));
        }

        @Test
        @DisplayName("debería lanzar MalformedJwtException con formato incorrecto de partes")
        void shouldThrowMalformedJwtExceptionWithIncorrectPartFormat() {
            assertThrows(MalformedJwtException.class,
                    () -> jwtUtil.extractUsername("only-one-part"));
        }
    }

    // ── Validación de Tokens ──────────────────────────────────────────────

    @Nested
    @DisplayName("Validación de Tokens")
    class TokenValidationTests {

        @Test
        @DisplayName("debería validar token correcto exitosamente")
        void shouldValidateCorrectTokenSuccessfully() {
            String token = jwtUtil.generateToken(userDetails);

            assertTrue(jwtUtil.validateToken(token, userDetails));
        }

        @Test
        @DisplayName("debería rechazar token con username incorrecto")
        void shouldRejectTokenWithWrongUsername() {
            String token = jwtUtil.generateToken(userDetails);
            UserDetails differentUser = mock(UserDetails.class);
            when(differentUser.getUsername()).thenReturn("different@example.com");

            assertFalse(jwtUtil.validateToken(token, differentUser));
        }

        @Test
        @DisplayName("debería rechazar token expirado")
        void shouldRejectExpiredToken() throws InterruptedException {
            ReflectionTestUtils.setField(jwtUtil, "expiration", SHORT_EXPIRATION);
            String token = jwtUtil.generateToken(userDetails);
            Thread.sleep(1500);

            assertFalse(jwtUtil.validateToken(token, userDetails));
        }

        @Test
        @DisplayName("debería manejar token malformado en validación")
        void shouldHandleMalformedTokenInValidation() {
            assertThrows(MalformedJwtException.class,
                    () -> jwtUtil.validateToken("malformed.token", userDetails));
        }

        @Test
        @DisplayName("debería validar token recién generado")
        void shouldValidateFreshlyGeneratedToken() {
            String token = jwtUtil.generateToken(userDetails);

            assertTrue(jwtUtil.validateToken(token, userDetails));
            verify(userDetails, atLeastOnce()).getUsername();
        }
    }

    // ── Verificación de Expiración ────────────────────────────────────────

    @Nested
    @DisplayName("Verificación de Expiración")
    class ExpirationTests {

        @Test
        @DisplayName("debería detectar token no expirado")
        void shouldDetectNonExpiredToken() {
            String token = jwtUtil.generateToken(userDetails);

            assertTrue(jwtUtil.extractExpiration(token).after(new Date()),
                    "Token no debe estar expirado");
        }

        @Test
        @DisplayName("debería extraer fecha de expiración correcta")
        void shouldExtractCorrectExpirationDate() {
            long beforeGeneration = System.currentTimeMillis();
            String token = jwtUtil.generateToken(userDetails);
            long afterGeneration = System.currentTimeMillis();

            Date expiration = jwtUtil.extractExpiration(token);

            assertNotNull(expiration);
            long actual = expiration.getTime();
            assertTrue(
                    actual >= beforeGeneration + TEST_EXPIRATION - 1000 && // FIX: -1000ms por truncado a segundos en JWT
                            actual <= afterGeneration  + TEST_EXPIRATION,
                    "Expiración debe estar dentro del rango esperado"
            );
        }

        @Test
        @DisplayName("debería detectar token expirado correctamente")
        void shouldDetectExpiredTokenCorrectly() throws InterruptedException {
            ReflectionTestUtils.setField(jwtUtil, "expiration", SHORT_EXPIRATION);
            String token = jwtUtil.generateToken(userDetails);
            Thread.sleep(1500);

            assertThrows(ExpiredJwtException.class,
                    () -> jwtUtil.extractExpiration(token));
        }

        @Test
        @DisplayName("debería retornar tiempo de expiración configurado")
        void shouldReturnConfiguredExpirationTime() {
            assertEquals(TEST_EXPIRATION, jwtUtil.getExpirationTime());
        }
    }

    // ── Password Reset Tokens ─────────────────────────────────────────────

    @Nested
    @DisplayName("Tokens de Reestablecimiento de Contraseña")
    class PasswordResetTokenTests {

        @Test
        @DisplayName("debería generar token de reset de contraseña válido")
        void shouldGenerateValidPasswordResetToken() {
            String token = jwtUtil.createPasswordResetToken(TEST_EMAIL, 900000L);

            assertNotNull(token);
            assertEquals(3, token.split("\\.").length);
        }

        @Test
        @DisplayName("debería validar token de reset de contraseña correcto")
        void shouldValidateCorrectPasswordResetToken() {
            String token = jwtUtil.createPasswordResetToken(TEST_EMAIL, 900000L);

            assertTrue(jwtUtil.validatePasswordResetToken(token));
        }

        @Test
        @DisplayName("debería rechazar token de reset expirado")
        void shouldRejectExpiredPasswordResetToken() throws InterruptedException {
            String token = jwtUtil.createPasswordResetToken(TEST_EMAIL, 500L);
            Thread.sleep(1000);

            assertFalse(jwtUtil.validatePasswordResetToken(token));
        }

        @Test
        @DisplayName("debería rechazar token de autenticación normal como token de reset")
        void shouldRejectNormalAuthTokenAsResetToken() {
            String normalToken = jwtUtil.generateToken(userDetails);

            assertFalse(jwtUtil.validatePasswordResetToken(normalToken),
                    "Token normal no debe validarse como token de reset");
        }

        @Test
        @DisplayName("debería extraer email correcto de token de reset")
        void shouldExtractCorrectEmailFromResetToken() {
            String token = jwtUtil.createPasswordResetToken(TEST_EMAIL, 900000L);

            assertEquals(TEST_EMAIL, jwtUtil.extractEmailFromPasswordResetToken(token));
        }

        @Test
        @DisplayName("debería retornar false para token malformado en validación de reset")
        void shouldReturnFalseForMalformedTokenInResetValidation() {
            assertFalse(jwtUtil.validatePasswordResetToken("malformed.token"));
        }

        @Test
        @DisplayName("debería generar tokens de reset con expiraciones diferentes")
        void shouldGenerateDifferentResetTokensWithDifferentExpirations() {
            String token1 = jwtUtil.createPasswordResetToken(TEST_EMAIL, 300000L);
            String token2 = jwtUtil.createPasswordResetToken(TEST_EMAIL, 900000L);

            assertNotEquals(token1, token2);
            assertTrue(jwtUtil.extractExpiration(token2).after(jwtUtil.extractExpiration(token1)),
                    "Token2 debe expirar después que Token1");
        }
    }

    // ── User ID desde SecurityContext ─────────────────────────────────────

    @Nested
    @DisplayName("Extracción de User ID desde Contexto de Seguridad")
    class SecurityContextUserIdTests {

        @BeforeEach
        void setUpSecurityContext() {
            SecurityContextHolder.clearContext();
        }

        @AfterEach
        void tearDownSecurityContext() {
            SecurityContextHolder.clearContext();
        }

        @Test
        @DisplayName("debería extraer ID de usuario desde CustomUserDetails")
        void shouldExtractUserIdFromCustomUserDetails() {
            when(customUserDetails.getId()).thenReturn(123);

            Authentication auth = mock(Authentication.class);
            when(auth.isAuthenticated()).thenReturn(true);
            when(auth.getPrincipal()).thenReturn(customUserDetails);

            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(securityContext);

            assertEquals(123, jwtUtil.getUserIdFromContext());
        }

        @Test
        @DisplayName("debería lanzar InvalidTokenException cuando Authentication es null")
        void shouldThrowInvalidTokenExceptionWhenAuthenticationIsNull() {
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(null);
            SecurityContextHolder.setContext(securityContext);

            InvalidTokenException ex = assertThrows(InvalidTokenException.class,
                    () -> jwtUtil.getUserIdFromContext());
            assertEquals("Usuario no autenticado", ex.getMessage());
        }

        @Test
        @DisplayName("debería lanzar InvalidTokenException cuando usuario no está autenticado")
        void shouldThrowInvalidTokenExceptionWhenUserNotAuthenticated() {
            Authentication auth = mock(Authentication.class);
            when(auth.isAuthenticated()).thenReturn(false);

            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(securityContext);

            InvalidTokenException ex = assertThrows(InvalidTokenException.class,
                    () -> jwtUtil.getUserIdFromContext());
            assertEquals("Usuario no autenticado", ex.getMessage());
        }

        @Test
        @DisplayName("debería lanzar InvalidTokenException cuando principal no es CustomUserDetails")
        void shouldThrowInvalidTokenExceptionWhenPrincipalIsNotCustomUserDetails() {
            Authentication auth = mock(Authentication.class);
            when(auth.isAuthenticated()).thenReturn(true);
            when(auth.getPrincipal()).thenReturn("string-principal");

            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(securityContext);

            InvalidTokenException ex = assertThrows(InvalidTokenException.class,
                    () -> jwtUtil.getUserIdFromContext());
            assertEquals("No se puede extraer el ID", ex.getMessage());
        }
    }

    // ── Casos Edge ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Casos Edge y Seguridad")
    class EdgeCasesAndSecurityTests {

        @Test
        @DisplayName("debería rechazar token con solo dos partes")
        void shouldRejectTokenWithOnlyTwoParts() {
            assertThrows(MalformedJwtException.class,
                    () -> jwtUtil.extractUsername("header.payload"));
        }

        @Test
        @DisplayName("debería rechazar token con cuatro partes")
        void shouldRejectTokenWithFourParts() {
            assertThrows(MalformedJwtException.class,
                    () -> jwtUtil.extractUsername("part1.part2.part3.part4"));
        }

        @Test
        @DisplayName("debería manejar username con espacios")
        void shouldHandleUsernameWithSpaces() {
            when(userDetails.getUsername()).thenReturn("user name@example.com");

            String token = jwtUtil.generateToken(userDetails);

            assertEquals("user name@example.com", jwtUtil.extractUsername(token));
        }

        @Test
        @DisplayName("debería manejar username muy largo")
        void shouldHandleVeryLongUsername() {
            String longUsername = "a".repeat(250) + "@example.com";
            when(userDetails.getUsername()).thenReturn(longUsername);

            String token = jwtUtil.generateToken(userDetails);

            assertEquals(longUsername, jwtUtil.extractUsername(token));
        }

        @Test
        @DisplayName("debería rechazar token con caracteres Base64 inválidos")
        void shouldRejectTokenWithInvalidBase64Characters() {
            assertThrows(Exception.class,
                    () -> jwtUtil.extractUsername("!!!.@@@.###"));
        }
    }
}
