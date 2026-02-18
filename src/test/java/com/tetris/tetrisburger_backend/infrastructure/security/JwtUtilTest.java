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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collection;
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

    // Secret key válido en Base64 (256 bits para HS256)
    private static final String TEST_SECRET = "dGVzdFNlY3JldEtleUZvckpXVFRva2VuVGVzdGluZ1dpdGhTcHJpbmdCb290QW5kU2VjdXJpdHkxMjM0NTY=";
    private static final Long TEST_EXPIRATION = 3600000L; // 1 hora
    private static final Long SHORT_EXPIRATION = 1000L; // 1 segundo
    private static final String TEST_EMAIL = "test@tetrisburger.com";

    @BeforeEach
    void setUp() {
        // Inyectar valores de @Value usando ReflectionTestUtils
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", TEST_EXPIRATION);

        // Configurar mock de UserDetails
        when(userDetails.getUsername()).thenReturn(TEST_EMAIL);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        when(userDetails.getAuthorities()).thenReturn((Collection) authorities);;
    }

    @Nested
    @DisplayName("Generación de Tokens de Autenticación")
    class AuthTokenGenerationTests {

        @Test
        @DisplayName("debería generar token válido con UserDetails")
        void shouldGenerateValidTokenWithUserDetails() {
            // When
            String token = jwtUtil.generateToken(userDetails);

            // Then
            assertNotNull(token);
            assertTrue(token.length() > 0);
            String[] parts = token.split("\\.");
            assertEquals(3, parts.length, "JWT debe tener 3 partes (header.payload.signature)");
        }

        @Test
        @DisplayName("debería generar token con estructura JWT válida")
        void shouldGenerateTokenWithValidJwtStructure() {
            // When
            String token = jwtUtil.generateToken(userDetails);

            // Then
            String[] parts = token.split("\\.");
            assertTrue(parts[0].length() > 0, "Header no debe estar vacío");
            assertTrue(parts[1].length() > 0, "Payload no debe estar vacío");
            assertTrue(parts[2].length() > 0, "Signature no debe estar vacía");
        }

        @Test
        @DisplayName("debería generar tokens diferentes para llamadas consecutivas")
        void shouldGenerateDifferentTokensForConsecutiveCalls() throws InterruptedException {
            // When
            String token1 = jwtUtil.generateToken(userDetails);
            Thread.sleep(10); // Asegurar diferente timestamp
            String token2 = jwtUtil.generateToken(userDetails);

            // Then
            assertNotEquals(token1, token2, "Tokens deben ser diferentes por timestamp");
        }

        @Test
        @DisplayName("debería lanzar excepción con UserDetails nulo")
        void shouldThrowExceptionWithNullUserDetails() {
            // When & Then
            assertThrows(NullPointerException.class,
                    () -> jwtUtil.generateToken(null));
        }

        @Test
        @DisplayName("debería generar token con username que contiene caracteres especiales")
        void shouldGenerateTokenWithSpecialCharactersInUsername() {
            // Given
            when(userDetails.getUsername()).thenReturn("user+test@example.com");

            // When
            String token = jwtUtil.generateToken(userDetails);

            // Then
            assertNotNull(token);
            assertEquals("user+test@example.com", jwtUtil.extractUsername(token));
        }
    }

    @Nested
    @DisplayName("Extracción de Username")
    class UsernameExtractionTests {

        @Test
        @DisplayName("debería extraer username correcto del token")
        void shouldExtractCorrectUsername() {
            // Given
            String token = jwtUtil.generateToken(userDetails);

            // When
            String extractedUsername = jwtUtil.extractUsername(token);

            // Then
            assertEquals(TEST_EMAIL, extractedUsername);
        }

        @Test
        @DisplayName("debería lanzar MalformedJwtException con token malformado")
        void shouldThrowMalformedJwtExceptionWithMalformedToken() {
            // Given
            String malformedToken = "invalid.token.structure";

            // When & Then
            assertThrows(MalformedJwtException.class,
                    () -> jwtUtil.extractUsername(malformedToken));
        }

        @Test
        @DisplayName("debería lanzar excepción con token vacío")
        void shouldThrowExceptionWithEmptyToken() {
            // Given
            String emptyToken = "";

            // When & Then
            assertThrows(Exception.class,
                    () -> jwtUtil.extractUsername(emptyToken));
        }

        @Test
        @DisplayName("debería lanzar excepción con token null")
        void shouldThrowExceptionWithNullToken() {
            // When & Then
            assertThrows(Exception.class,
                    () -> jwtUtil.extractUsername(null));
        }

        @Test
        @DisplayName("debería lanzar SignatureException con token con firma alterada")
        void shouldThrowSignatureExceptionWithTamperedToken() {
            // Given
            String token = jwtUtil.generateToken(userDetails);
            String tamperedToken = token.substring(0, token.length() - 10) + "XXXXXXXXXX";

            // When & Then
            assertThrows(SignatureException.class,
                    () -> jwtUtil.extractUsername(tamperedToken));
        }

        @Test
        @DisplayName("debería lanzar MalformedJwtException con formato incorrecto de partes")
        void shouldThrowMalformedJwtExceptionWithIncorrectPartFormat() {
            // Given
            String invalidToken = "only-one-part";

            // When & Then
            assertThrows(MalformedJwtException.class,
                    () -> jwtUtil.extractUsername(invalidToken));
        }
    }

    @Nested
    @DisplayName("Validación de Tokens")
    class TokenValidationTests {

        @Test
        @DisplayName("debería validar token correcto exitosamente")
        void shouldValidateCorrectTokenSuccessfully() {
            // Given
            String token = jwtUtil.generateToken(userDetails);

            // When
            Boolean isValid = jwtUtil.validateToken(token, userDetails);

            // Then
            assertTrue(isValid);
        }

        @Test
        @DisplayName("debería rechazar token con username incorrecto")
        void shouldRejectTokenWithWrongUsername() {
            // Given
            String token = jwtUtil.generateToken(userDetails);
            UserDetails differentUser = mock(UserDetails.class);
            when(differentUser.getUsername()).thenReturn("different@example.com");

            // When
            Boolean isValid = jwtUtil.validateToken(token, differentUser);

            // Then
            assertFalse(isValid);
        }

        @Test
        @DisplayName("debería rechazar token expirado")
        void shouldRejectExpiredToken() throws InterruptedException {
            // Given - configurar expiración corta
            ReflectionTestUtils.setField(jwtUtil, "expiration", SHORT_EXPIRATION);
            String token = jwtUtil.generateToken(userDetails);
            Thread.sleep(1500); // Esperar que expire

            // When
            Boolean isValid = jwtUtil.validateToken(token, userDetails);

            // Then
            assertFalse(isValid);
        }

        @Test
        @DisplayName("debería manejar token malformado en validación")
        void shouldHandleMalformedTokenInValidation() {
            // Given
            String malformedToken = "malformed.token";

            // When & Then
            assertThrows(MalformedJwtException.class,
                    () -> jwtUtil.validateToken(malformedToken, userDetails));
        }

        @Test
        @DisplayName("debería validar token recién generado")
        void shouldValidateFreshlyGeneratedToken() {
            // Given
            String token = jwtUtil.generateToken(userDetails);

            // When
            Boolean isValid = jwtUtil.validateToken(token, userDetails);

            // Then
            assertTrue(isValid);
            verify(userDetails, atLeastOnce()).getUsername();
        }
    }

    @Nested
    @DisplayName("Verificación de Expiración")
    class ExpirationTests {

        @Test
        @DisplayName("debería detectar token no expirado")
        void shouldDetectNonExpiredToken() {
            // Given
            String token = jwtUtil.generateToken(userDetails);

            // When
            Date expiration = jwtUtil.extractExpiration(token);

            // Then
            assertTrue(expiration.after(new Date()), "Token no debe estar expirado");
        }

        @Test
        @DisplayName("debería extraer fecha de expiración correcta")
        void shouldExtractCorrectExpirationDate() {
            // Given
            long beforeGeneration = System.currentTimeMillis();
            String token = jwtUtil.generateToken(userDetails);
            long afterGeneration = System.currentTimeMillis();

            // When
            Date expiration = jwtUtil.extractExpiration(token);

            // Then
            assertNotNull(expiration);
            long expectedExpiration = beforeGeneration + TEST_EXPIRATION;
            long actualExpiration = expiration.getTime();
            assertTrue(actualExpiration >= expectedExpiration &&
                            actualExpiration <= afterGeneration + TEST_EXPIRATION,
                    "Expiración debe estar dentro del rango esperado");
        }

        @Test
        @DisplayName("debería detectar token expirado correctamente")
        void shouldDetectExpiredTokenCorrectly() throws InterruptedException {
            // Given
            ReflectionTestUtils.setField(jwtUtil, "expiration", SHORT_EXPIRATION);
            String token = jwtUtil.generateToken(userDetails);
            Thread.sleep(1500);

            // When & Then
            assertThrows(ExpiredJwtException.class,
                    () -> jwtUtil.extractExpiration(token));
        }

        @Test
        @DisplayName("debería retornar tiempo de expiración configurado")
        void shouldReturnConfiguredExpirationTime() {
            // When
            Long expirationTime = jwtUtil.getExpirationTime();

            // Then
            assertEquals(TEST_EXPIRATION, expirationTime);
        }
    }

    @Nested
    @DisplayName("Tokens de Reestablecimiento de Contraseña")
    class PasswordResetTokenTests {

        @Test
        @DisplayName("debería generar token de reset de contraseña válido")
        void shouldGenerateValidPasswordResetToken() {
            // Given
            long resetExpiration = 900000L; // 15 minutos

            // When
            String token = jwtUtil.createPasswordResetToken(TEST_EMAIL, resetExpiration);

            // Then
            assertNotNull(token);
            String[] parts = token.split("\\.");
            assertEquals(3, parts.length);
        }

        @Test
        @DisplayName("debería validar token de reset de contraseña correcto")
        void shouldValidateCorrectPasswordResetToken() {
            // Given
            long resetExpiration = 900000L;
            String token = jwtUtil.createPasswordResetToken(TEST_EMAIL, resetExpiration);

            // When
            boolean isValid = jwtUtil.validatePasswordResetToken(token);

            // Then
            assertTrue(isValid);
        }

        @Test
        @DisplayName("debería rechazar token de reset expirado")
        void shouldRejectExpiredPasswordResetToken() throws InterruptedException {
            // Given
            long shortExpiration = 500L;
            String token = jwtUtil.createPasswordResetToken(TEST_EMAIL, shortExpiration);
            Thread.sleep(1000);

            // When
            boolean isValid = jwtUtil.validatePasswordResetToken(token);

            // Then
            assertFalse(isValid);
        }

        @Test
        @DisplayName("debería rechazar token de autenticación normal como token de reset")
        void shouldRejectNormalAuthTokenAsResetToken() {
            // Given
            String normalToken = jwtUtil.generateToken(userDetails);

            // When
            boolean isValid = jwtUtil.validatePasswordResetToken(normalToken);

            // Then
            assertFalse(isValid, "Token normal no debe validarse como token de reset");
        }

        @Test
        @DisplayName("debería extraer email correcto de token de reset")
        void shouldExtractCorrectEmailFromResetToken() {
            // Given
            long resetExpiration = 900000L;
            String token = jwtUtil.createPasswordResetToken(TEST_EMAIL, resetExpiration);

            // When
            String extractedEmail = jwtUtil.extractEmailFromPasswordResetToken(token);

            // Then
            assertEquals(TEST_EMAIL, extractedEmail);
        }

        @Test
        @DisplayName("debería retornar false para token malformado en validación de reset")
        void shouldReturnFalseForMalformedTokenInResetValidation() {
            // Given
            String malformedToken = "malformed.token";

            // When
            boolean isValid = jwtUtil.validatePasswordResetToken(malformedToken);

            // Then
            assertFalse(isValid);
        }

        @Test
        @DisplayName("debería generar tokens de reset diferentes con tiempos de expiración diferentes")
        void shouldGenerateDifferentResetTokensWithDifferentExpirations() {
            // Given
            long expiration1 = 300000L; // 5 minutos
            long expiration2 = 900000L; // 15 minutos

            // When
            String token1 = jwtUtil.createPasswordResetToken(TEST_EMAIL, expiration1);
            String token2 = jwtUtil.createPasswordResetToken(TEST_EMAIL, expiration2);

            // Then
            assertNotEquals(token1, token2);
            Date exp1 = jwtUtil.extractExpiration(token1);
            Date exp2 = jwtUtil.extractExpiration(token2);
            assertTrue(exp2.after(exp1), "Token2 debe expirar después que Token1");
        }
    }

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
            // Given
            Integer expectedUserId = 123;
            when(customUserDetails.getId()).thenReturn(expectedUserId);

            Authentication auth = mock(Authentication.class);
            when(auth.isAuthenticated()).thenReturn(true);
            when(auth.getPrincipal()).thenReturn(customUserDetails);

            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(securityContext);

            // When
            Integer userId = jwtUtil.getUserIdFromContext();

            // Then
            assertEquals(expectedUserId, userId);
        }

        @Test
        @DisplayName("debería lanzar InvalidTokenException cuando Authentication es null")
        void shouldThrowInvalidTokenExceptionWhenAuthenticationIsNull() {
            // Given
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(null);
            SecurityContextHolder.setContext(securityContext);

            // When & Then
            InvalidTokenException exception = assertThrows(InvalidTokenException.class,
                    () -> jwtUtil.getUserIdFromContext());
            assertEquals("Usuario no autenticado", exception.getMessage());
        }

        @Test
        @DisplayName("debería lanzar InvalidTokenException cuando usuario no está autenticado")
        void shouldThrowInvalidTokenExceptionWhenUserNotAuthenticated() {
            // Given
            Authentication auth = mock(Authentication.class);
            when(auth.isAuthenticated()).thenReturn(false);

            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(securityContext);

            // When & Then
            InvalidTokenException exception = assertThrows(InvalidTokenException.class,
                    () -> jwtUtil.getUserIdFromContext());
            assertEquals("Usuario no autenticado", exception.getMessage());
        }

        @Test
        @DisplayName("debería lanzar InvalidTokenException cuando principal no es CustomUserDetails")
        void shouldThrowInvalidTokenExceptionWhenPrincipalIsNotCustomUserDetails() {
            // Given
            Authentication auth = mock(Authentication.class);
            when(auth.isAuthenticated()).thenReturn(true);
            when(auth.getPrincipal()).thenReturn("string-principal"); // No es CustomUserDetails

            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(securityContext);

            // When & Then
            InvalidTokenException exception = assertThrows(InvalidTokenException.class,
                    () -> jwtUtil.getUserIdFromContext());
            assertEquals("No se puede extraer el ID", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Casos Edge y Seguridad")
    class EdgeCasesAndSecurityTests {

        @Test
        @DisplayName("debería rechazar token con solo dos partes")
        void shouldRejectTokenWithOnlyTwoParts() {
            // Given
            String invalidToken = "header.payload";

            // When & Then
            assertThrows(MalformedJwtException.class,
                    () -> jwtUtil.extractUsername(invalidToken));
        }

        @Test
        @DisplayName("debería rechazar token con cuatro partes")
        void shouldRejectTokenWithFourParts() {
            // Given
            String invalidToken = "part1.part2.part3.part4";

            // When & Then
            assertThrows(MalformedJwtException.class,
                    () -> jwtUtil.extractUsername(invalidToken));
        }

        @Test
        @DisplayName("debería manejar username con espacios")
        void shouldHandleUsernameWithSpaces() {
            // Given
            when(userDetails.getUsername()).thenReturn("user name@example.com");

            // When
            String token = jwtUtil.generateToken(userDetails);
            String extractedUsername = jwtUtil.extractUsername(token);

            // Then
            assertEquals("user name@example.com", extractedUsername);
        }

        @Test
        @DisplayName("debería manejar username muy largo")
        void shouldHandleVeryLongUsername() {
            // Given
            String longUsername = "a".repeat(250) + "@example.com";
            when(userDetails.getUsername()).thenReturn(longUsername);

            // When
            String token = jwtUtil.generateToken(userDetails);
            String extractedUsername = jwtUtil.extractUsername(token);

            // Then
            assertEquals(longUsername, extractedUsername);
        }

        @Test
        @DisplayName("debería rechazar token con caracteres Base64 inválidos")
        void shouldRejectTokenWithInvalidBase64Characters() {
            // Given
            String invalidToken = "!!!.@@@.###";

            // When & Then
            assertThrows(Exception.class,
                    () -> jwtUtil.extractUsername(invalidToken));
        }
    }
}
