package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.tetris.tetrisburger_backend.domain.exception.InvalidTokenException;
import com.tetris.tetrisburger_backend.domain.port.out.GoogleAuthPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de GoogleAuthAdapter")
class GoogleAuthAdapterTest {

    @Mock
    private GoogleIdTokenVerifier verifier;

    @Mock
    private GoogleIdToken idToken;

    @Mock
    private GoogleIdToken.Payload payload;

    private GoogleAuthAdapter googleAuthAdapter;

    private static final String VALID_TOKEN = "valid.google.token";
    private static final String CLIENT_ID = "test-client-id.apps.googleusercontent.com";

    @BeforeEach
    void setUp() {
        // Crear adapter con CLIENT_ID de prueba
        googleAuthAdapter = new GoogleAuthAdapter(CLIENT_ID);

        // Inyectar el verifier mockeado
        ReflectionTestUtils.setField(googleAuthAdapter, "verifier", verifier);
    }

    @Nested
    @DisplayName("validateAndExtractUserInfo - Casos de éxito")
    class SuccessfulValidation {

        @Test
        @DisplayName("debería validar token y extraer información del usuario")
        void shouldValidateTokenAndExtractUserInfo() throws Exception {
            // Given
            String email = "user@example.com";
            String userName = "John Doe";

            when(verifier.verify(VALID_TOKEN)).thenReturn(idToken);
            when(idToken.getPayload()).thenReturn(payload);
            when(payload.getEmail()).thenReturn(email);
            when(payload.get("name")).thenReturn(userName);

            // When
            Map<String, String> userInfo = googleAuthAdapter.validateAndExtractUserInfo(VALID_TOKEN);

            // Then
            assertThat(userInfo).isNotNull();
            assertThat(userInfo).hasSize(2);
            assertThat(userInfo.get("email")).isEqualTo(email);
            assertThat(userInfo.get("userName")).isEqualTo(userName);

            verify(verifier).verify(VALID_TOKEN);
            verify(idToken).getPayload();
        }

        @Test
        @DisplayName("debería extraer información con caracteres especiales")
        void shouldExtractInfoWithSpecialCharacters() throws Exception {
            // Given
            String email = "josé.garcía@example.com";
            String userName = "José García Pérez";

            when(verifier.verify(VALID_TOKEN)).thenReturn(idToken);
            when(idToken.getPayload()).thenReturn(payload);
            when(payload.getEmail()).thenReturn(email);
            when(payload.get("name")).thenReturn(userName);

            // When
            Map<String, String> userInfo = googleAuthAdapter.validateAndExtractUserInfo(VALID_TOKEN);

            // Then
            assertThat(userInfo.get("email")).isEqualTo(email);
            assertThat(userInfo.get("userName")).isEqualTo(userName);
        }
    }

    @Nested
    @DisplayName("validateAndExtractUserInfo - Token inválido")
    class InvalidToken {

        @Test
        @DisplayName("debería lanzar InvalidTokenException cuando el token es null")
        void shouldThrowExceptionWhenTokenIsNull() throws Exception {
            // Given
            when(verifier.verify(anyString())).thenReturn(null);

            // When/Then
            assertThatThrownBy(() -> googleAuthAdapter.validateAndExtractUserInfo("invalid-token"))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessageContaining("Token de Google inválido o expirado");

            verify(verifier).verify("invalid-token");
        }

        @Test
        @DisplayName("debería lanzar InvalidTokenException cuando ocurre GeneralSecurityException")
        void shouldThrowExceptionOnGeneralSecurityException() throws Exception {
            // Given
            when(verifier.verify(anyString()))
                    .thenThrow(new GeneralSecurityException("Invalid signature"));

            // When/Then
            assertThatThrownBy(() -> googleAuthAdapter.validateAndExtractUserInfo(VALID_TOKEN))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessageContaining("Fallo en la validación del token de Google");
        }

        @Test
        @DisplayName("debería lanzar InvalidTokenException cuando ocurre IOException")
        void shouldThrowExceptionOnIOException() throws Exception {
            // Given
            when(verifier.verify(anyString()))
                    .thenThrow(new IOException("Network error"));

            // When/Then
            assertThatThrownBy(() -> googleAuthAdapter.validateAndExtractUserInfo(VALID_TOKEN))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessageContaining("Fallo en la validación del token de Google");
        }

        @Test
        @DisplayName("debería lanzar InvalidTokenException cuando ocurre IllegalArgumentException")
        void shouldThrowExceptionOnIllegalArgumentException() throws Exception {
            // Given
            when(verifier.verify(anyString()))
                    .thenThrow(new IllegalArgumentException("Invalid format"));

            // When/Then
            assertThatThrownBy(() -> googleAuthAdapter.validateAndExtractUserInfo(VALID_TOKEN))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessageContaining("Fallo en la validación del token de Google");
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("debería manejar nombre null en payload")
        void shouldHandleNullNameInPayload() throws Exception {
            // Given
            when(verifier.verify(VALID_TOKEN)).thenReturn(idToken);
            when(idToken.getPayload()).thenReturn(payload);
            when(payload.getEmail()).thenReturn("test@example.com");
            when(payload.get("name")).thenReturn(null);

            // When
            Map<String, String> userInfo = googleAuthAdapter.validateAndExtractUserInfo(VALID_TOKEN);

            // Then
            assertThat(userInfo.get("email")).isEqualTo("test@example.com");
            assertThat(userInfo.get("userName")).isNull(); // ✅ Corregido
        }

        @Test
        @DisplayName("debería extraer email correctamente aunque el nombre sea vacío")
        void shouldExtractEmailWhenNameIsEmpty() throws Exception {
            // Given
            when(verifier.verify(VALID_TOKEN)).thenReturn(idToken);
            when(idToken.getPayload()).thenReturn(payload);
            when(payload.getEmail()).thenReturn("user@example.com");
            when(payload.get("name")).thenReturn("");

            // When
            Map<String, String> userInfo = googleAuthAdapter.validateAndExtractUserInfo(VALID_TOKEN);

            // Then
            assertThat(userInfo.get("email")).isEqualTo("user@example.com");
            assertThat(userInfo.get("userName")).isEqualTo(""); // ✅ Esto está bien
        }
    }


    @Nested
    @DisplayName("Implementa GoogleAuthPort")
    class ImplementsGoogleAuthPort {

        @Test
        @DisplayName("debería implementar GoogleAuthPort")
        void shouldImplementGoogleAuthPort() {
            assertThat(googleAuthAdapter).isInstanceOf(GoogleAuthPort.class);
        }
    }
}
