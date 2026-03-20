package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.port.out.RecaptchaPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de RecaptchaAdapter")
class RecaptchaAdapterTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private RecaptchaAdapter recaptchaAdapter;

    private static final String SECRET_KEY = "test-secret-key";
    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    private static final double THRESHOLD = 0.5;
    private static final String VALID_TOKEN = "valid-recaptcha-token";
    private static final String ACTION = "login";

    @BeforeEach
    void setUp() {
        WebClient.Builder webClientBuilder = mock(WebClient.Builder.class);
        when(webClientBuilder.build()).thenReturn(webClient);

        recaptchaAdapter = new RecaptchaAdapter(webClientBuilder);

        ReflectionTestUtils.setField(recaptchaAdapter, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(recaptchaAdapter, "verifyUrl", VERIFY_URL);
        ReflectionTestUtils.setField(recaptchaAdapter, "threshold", THRESHOLD);

        // Configurar el flujo de WebClient una vez con lenient()
        lenient().when(webClient.post()).thenReturn(requestBodyUriSpec);
        lenient().when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        lenient().doReturn(requestBodySpec).when(requestBodySpec).bodyValue(anyString());
        lenient().when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    }

    @Nested
    @DisplayName("verifyToken - Casos de éxito")
    class SuccessfulVerification {

        @Test
        @DisplayName("debería verificar token correctamente con threshold por defecto")
        void shouldVerifyTokenSuccessfully() throws Exception {
            // Given
            Object mockResponse = createMockResponse(true, 0.9, ACTION);
            when(responseSpec.bodyToMono(any(Class.class))).thenReturn(Mono.just(mockResponse));

            // When
            boolean result = recaptchaAdapter.verifyToken(VALID_TOKEN, ACTION);

            // Then
            assertThat(result).isTrue();
            verifyWebClientInteraction();
        }

        @Test
        @DisplayName("debería verificar token con score exactamente igual al threshold")
        void shouldVerifyTokenWithScoreEqualToThreshold() throws Exception {
            // Given
            Object mockResponse = createMockResponse(true, THRESHOLD, ACTION);
            when(responseSpec.bodyToMono(any(Class.class))).thenReturn(Mono.just(mockResponse));

            // When
            boolean result = recaptchaAdapter.verifyToken(VALID_TOKEN, ACTION, THRESHOLD);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("debería verificar token con threshold personalizado")
        void shouldVerifyTokenWithCustomThreshold() throws Exception {
            // Given
            double customThreshold = 0.7;
            Object mockResponse = createMockResponse(true, 0.8, ACTION);
            when(responseSpec.bodyToMono(any(Class.class))).thenReturn(Mono.just(mockResponse));

            // When
            boolean result = recaptchaAdapter.verifyToken(VALID_TOKEN, ACTION, customThreshold);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("debería verificar token con score máximo")
        void shouldVerifyTokenWithMaxScore() throws Exception {
            // Given
            Object mockResponse = createMockResponse(true, 1.0, ACTION);
            when(responseSpec.bodyToMono(any(Class.class))).thenReturn(Mono.just(mockResponse));

            // When
            boolean result = recaptchaAdapter.verifyToken(VALID_TOKEN, ACTION);

            // Then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("verifyToken - Fallos")
    class FailedVerification {

        @Test
        @DisplayName("debería fallar cuando success es false")
        void shouldFailWhenSuccessIsFalse() throws Exception {
            // Given
            Object mockResponse = createMockResponse(false, 0.0, ACTION);
            when(responseSpec.bodyToMono(any(Class.class))).thenReturn(Mono.just(mockResponse));

            // When
            boolean result = recaptchaAdapter.verifyToken(VALID_TOKEN, ACTION);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("debería fallar cuando la acción no coincide")
        void shouldFailWhenActionDoesNotMatch() throws Exception {
            // Given
            Object mockResponse = createMockResponse(true, 0.9, "different_action");
            when(responseSpec.bodyToMono(any(Class.class))).thenReturn(Mono.just(mockResponse));

            // When
            boolean result = recaptchaAdapter.verifyToken(VALID_TOKEN, ACTION);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("debería fallar cuando el score es menor al threshold")
        void shouldFailWhenScoreBelowThreshold() throws Exception {
            // Given
            Object mockResponse = createMockResponse(true, 0.3, ACTION);
            when(responseSpec.bodyToMono(any(Class.class))).thenReturn(Mono.just(mockResponse));

            // When
            boolean result = recaptchaAdapter.verifyToken(VALID_TOKEN, ACTION);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("debería fallar cuando la respuesta es null")
        void shouldFailWhenResponseIsNull() {
            // Given
            when(responseSpec.bodyToMono(any(Class.class))).thenReturn(Mono.empty());

            // When
            boolean result = recaptchaAdapter.verifyToken(VALID_TOKEN, ACTION);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("debería fallar cuando ocurre una excepción")
        void shouldFailWhenExceptionOccurs() {
            // Given
            when(webClient.post()).thenThrow(new RuntimeException("Network error"));

            // When
            boolean result = recaptchaAdapter.verifyToken(VALID_TOKEN, ACTION);

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("debería manejar score de 0.0")
        void shouldHandleZeroScore() throws Exception {
            // Given
            Object mockResponse = createMockResponse(true, 0.0, ACTION);
            when(responseSpec.bodyToMono(any(Class.class))).thenReturn(Mono.just(mockResponse));

            // When
            boolean result = recaptchaAdapter.verifyToken(VALID_TOKEN, ACTION);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("debería manejar threshold muy bajo")
        void shouldHandleVeryLowThreshold() throws Exception {
            // Given
            double lowThreshold = 0.1;
            Object mockResponse = createMockResponse(true, 0.2, ACTION);
            when(responseSpec.bodyToMono(any(Class.class))).thenReturn(Mono.just(mockResponse));

            // When
            boolean result = recaptchaAdapter.verifyToken(VALID_TOKEN, ACTION, lowThreshold);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("debería manejar threshold muy alto")
        void shouldHandleVeryHighThreshold() throws Exception {
            // Given
            double highThreshold = 0.95;
            Object mockResponse = createMockResponse(true, 0.9, ACTION);
            when(responseSpec.bodyToMono(any(Class.class))).thenReturn(Mono.just(mockResponse));

            // When
            boolean result = recaptchaAdapter.verifyToken(VALID_TOKEN, ACTION, highThreshold);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("debería verificar con diferentes acciones")
        void shouldVerifyWithDifferentActions() throws Exception {
            // Given
            String action = "register";
            Object mockResponse = createMockResponse(true, 0.8, action);
            when(responseSpec.bodyToMono(any(Class.class))).thenReturn(Mono.just(mockResponse));

            // When
            boolean result = recaptchaAdapter.verifyToken(VALID_TOKEN, action);

            // Then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("Implementa RecaptchaPort")
    class ImplementsRecaptchaPort {

        @Test
        @DisplayName("debería implementar RecaptchaPort")
        void shouldImplementRecaptchaPort() {
            assertThat(recaptchaAdapter).isInstanceOf(RecaptchaPort.class);
        }
    }

    // ====== HELPER METHODS ======

    /**
     * Crea una instancia de la clase interna RecaptchaResponse del adapter usando reflexión
     */
    private Object createMockResponse(boolean success, double score, String action) throws Exception {
        // Obtener la clase interna RecaptchaResponse
        Class<?> responseClass = Class.forName(
                "com.tetris.tetrisburger_backend.infrastructure.adapter.RecaptchaAdapter$RecaptchaResponse"
        );

        // Obtener el constructor y hacerlo accesible
        Constructor<?> constructor = responseClass.getDeclaredConstructor();
        constructor.setAccessible(true); // ✅ LÍNEA CLAVE AÑADIDA

        // Crear instancia
        Object instance = constructor.newInstance();

        // Setear campos usando reflexión
        setField(instance, "success", success);
        setField(instance, "score", score);
        setField(instance, "action", action);
        setField(instance, "challengeTs", "2026-02-16T22:00:00Z");
        setField(instance, "hostname", "localhost");

        return instance;
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private void verifyWebClientInteraction() {
        verify(webClient).post();
        verify(requestBodyUriSpec).uri(VERIFY_URL);
        verify(requestBodySpec).bodyValue(anyString());
        verify(requestBodySpec).retrieve();
    }
}
