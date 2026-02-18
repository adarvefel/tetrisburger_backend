package com.tetris.tetrisburger_backend.domain.common;

import com.tetris.tetrisburger_backend.domain.model.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Pruebas de LoginResponse")
class LoginResponseTest {

    @Nested
    @DisplayName("Creación Exitosa")
    class SuccessfulCreationTests {

        @Test
        @DisplayName("debería crear LoginResponse con valores válidos")
        void shouldCreateLoginResponseWithValidValues() {
            // Given
            String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
            User user = User.createClient("Test User", "test@example.com", "$2a$10$hashed");
            long expiresIn = 3600000L;

            // When
            LoginResponse response = new LoginResponse(token, user, expiresIn);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.token()).isEqualTo(token);
            assertThat(response.user()).isEqualTo(user);
            assertThat(response.expiresIn()).isEqualTo(3600000L);
        }

        @Test
        @DisplayName("debería ser inmutable (record)")
        void shouldBeImmutable() {
            // Given
            String token = "token123";
            User user = User.createClient("Test", "test@example.com", "$2a$10$hashed");
            LoginResponse response = new LoginResponse(token, user, 3600000L);

            // Then - Los valores no pueden cambiar después de la creación
            assertThat(response.token()).isEqualTo("token123");
            // No existen setters en un record
        }

        @Test
        @DisplayName("debería crear LoginResponse con token JWT real")
        void shouldCreateLoginResponseWithRealJWT() {
            // Given
            String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
            User user = User.createByAdmin(
                    "Admin User",
                    "admin@example.com",
                    "$2a$10$hashed",
                    Role.ADMIN,
                    null,
                    null,
                    null,
                    1
            );
            long expiresIn = 7200000L; // 2 horas

            // When
            LoginResponse response = new LoginResponse(jwtToken, user, expiresIn);

            // Then
            assertThat(response.token()).isEqualTo(jwtToken);
            assertThat(response.user().getRole()).isEqualTo(Role.ADMIN);
            assertThat(response.expiresIn()).isEqualTo(7200000L);
        }
    }

    @Nested
    @DisplayName("Validación de Token")
    class TokenValidationTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("debería lanzar excepción cuando token es nulo o vacío")
        void shouldThrowExceptionWhenTokenIsNullOrBlank(String invalidToken) {
            // Given
            User user = User.createClient("Test", "test@example.com", "$2a$10$hashed");

            // Then
            assertThatThrownBy(() ->
                    new LoginResponse(invalidToken, user, 3600000L)
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El token no puede estar vacío");
        }
    }

    @Nested
    @DisplayName("Validación de Usuario")
    class UserValidationTests {

        @Test
        @DisplayName("debería lanzar excepción cuando usuario es nulo")
        void shouldThrowExceptionWhenUserIsNull() {
            // Given
            String token = "validToken123";

            // Then
            assertThatThrownBy(() ->
                    new LoginResponse(token, null, 3600000L)
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El usuario no puede ser nulo");
        }
    }

    @Nested
    @DisplayName("Validación de Tiempo de Expiración")
    class ExpirationValidationTests {

        @ParameterizedTest
        @ValueSource(longs = {0, -1, -100, -3600000})
        @DisplayName("debería lanzar excepción cuando expiresIn es cero o negativo")
        void shouldThrowExceptionWhenExpiresInIsZeroOrNegative(long invalidExpiration) {
            // Given
            String token = "validToken123";
            User user = User.createClient("Test", "test@example.com", "$2a$10$hashed");

            // Then
            assertThatThrownBy(() ->
                    new LoginResponse(token, user, invalidExpiration)
            )
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El tiempo de expiración debe ser positivo");
        }

        @Test
        @DisplayName("debería aceptar expiresIn de 1 milisegundo")
        void shouldAcceptExpiresInOfOneMillisecond() {
            // Given
            String token = "validToken123";
            User user = User.createClient("Test", "test@example.com", "$2a$10$hashed");

            // When
            LoginResponse response = new LoginResponse(token, user, 1L);

            // Then
            assertThat(response.expiresIn()).isEqualTo(1L);
        }

        @Test
        @DisplayName("debería aceptar expiresIn muy grande")
        void shouldAcceptVeryLargeExpiresIn() {
            // Given
            String token = "validToken123";
            User user = User.createClient("Test", "test@example.com", "$2a$10$hashed");
            long thirtyDays = 30L * 24 * 60 * 60 * 1000; // 30 días en ms

            // When
            LoginResponse response = new LoginResponse(token, user, thirtyDays);

            // Then
            assertThat(response.expiresIn()).isEqualTo(thirtyDays);
        }
    }

    @Nested
    @DisplayName("Igualdad y HashCode")
    class EqualityTests {

        @Test
        @DisplayName("dos LoginResponse con los mismos valores deberían ser iguales")
        void twoLoginResponsesWithSameValuesShouldBeEqual() {
            // Given
            String token = "token123";
            User user = User.createClient("Test", "test@example.com", "$2a$10$hashed");
            long expiresIn = 3600000L;

            LoginResponse response1 = new LoginResponse(token, user, expiresIn);
            LoginResponse response2 = new LoginResponse(token, user, expiresIn);

            // Then
            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("dos LoginResponse con diferentes tokens no deberían ser iguales")
        void twoLoginResponsesWithDifferentTokensShouldNotBeEqual() {
            // Given
            User user = User.createClient("Test", "test@example.com", "$2a$10$hashed");

            LoginResponse response1 = new LoginResponse("token1", user, 3600000L);
            LoginResponse response2 = new LoginResponse("token2", user, 3600000L);

            // Then
            assertThat(response1).isNotEqualTo(response2);
        }
    }
}
