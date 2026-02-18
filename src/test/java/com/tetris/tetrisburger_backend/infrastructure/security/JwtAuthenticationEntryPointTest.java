package com.tetris.tetrisburger_backend.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de JwtAuthenticationEntryPoint")
class JwtAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private MockHttpServletResponse response;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        response = new MockHttpServletResponse();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("Pruebas de Respuesta HTTP")
    class HttpResponseTests {

        @Test
        @DisplayName("Debería establecer status code 401 Unauthorized")
        void shouldSetStatusCode401Unauthorized() throws IOException {
            // Given
            AuthenticationException authException = new BadCredentialsException("Invalid credentials");
            when(request.getServletPath()).thenReturn("/api/users/profile");

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
            assertThat(response.getStatus()).isEqualTo(401);
        }

        @Test
        @DisplayName("Debería establecer content type como application/json")
        void shouldSetContentTypeAsApplicationJson() throws IOException {
            // Given
            AuthenticationException authException = new BadCredentialsException("Invalid credentials");
            when(request.getServletPath()).thenReturn("/api/users/profile");

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        }

        @Test
        @DisplayName("Debería retornar respuesta en formato JSON válido")
        void shouldReturnValidJsonResponse() throws IOException {
            // Given
            AuthenticationException authException = new BadCredentialsException("Invalid credentials");
            when(request.getServletPath()).thenReturn("/api/users/profile");

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            String jsonResponse = response.getContentAsString();
            assertThat(jsonResponse).isNotEmpty();

            // Verificar que es JSON válido
            Map<String, Object> responseBody = objectMapper.readValue(jsonResponse, Map.class);
            assertThat(responseBody).isNotNull();
        }
    }

    @Nested
    @DisplayName("Pruebas de Cuerpo de Respuesta")
    class ResponseBodyTests {

        @Test
        @DisplayName("Debería incluir status 401 en el cuerpo de respuesta")
        void shouldIncludeStatus401InResponseBody() throws IOException {
            // Given
            AuthenticationException authException = new BadCredentialsException("Invalid credentials");
            when(request.getServletPath()).thenReturn("/api/users/profile");

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            String jsonResponse = response.getContentAsString();
            Map<String, Object> responseBody = objectMapper.readValue(jsonResponse, Map.class);

            assertThat(responseBody).containsKey("status");
            assertThat(responseBody.get("status")).isEqualTo(401);
        }

        @Test
        @DisplayName("Debería incluir error 'Unauthorized' en el cuerpo de respuesta")
        void shouldIncludeUnauthorizedErrorInResponseBody() throws IOException {
            // Given
            AuthenticationException authException = new BadCredentialsException("Invalid credentials");
            when(request.getServletPath()).thenReturn("/api/users/profile");

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            String jsonResponse = response.getContentAsString();
            Map<String, Object> responseBody = objectMapper.readValue(jsonResponse, Map.class);

            assertThat(responseBody).containsKey("error");
            assertThat(responseBody.get("error")).isEqualTo("Unauthorized");
        }

        @Test
        @DisplayName("Debería incluir mensaje descriptivo en el cuerpo de respuesta")
        void shouldIncludeDescriptiveMessageInResponseBody() throws IOException {
            // Given
            AuthenticationException authException = new BadCredentialsException("Invalid credentials");
            when(request.getServletPath()).thenReturn("/api/users/profile");

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            String jsonResponse = response.getContentAsString();
            Map<String, Object> responseBody = objectMapper.readValue(jsonResponse, Map.class);

            assertThat(responseBody).containsKey("message");
            assertThat(responseBody.get("message")).isEqualTo("Token inválido, expirado o no proporcionado");
        }

        @Test
        @DisplayName("Debería incluir path de la solicitud en el cuerpo de respuesta")
        void shouldIncludeRequestPathInResponseBody() throws IOException {
            // Given
            String servletPath = "/api/users/profile";
            AuthenticationException authException = new BadCredentialsException("Invalid credentials");
            when(request.getServletPath()).thenReturn(servletPath);

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            String jsonResponse = response.getContentAsString();
            Map<String, Object> responseBody = objectMapper.readValue(jsonResponse, Map.class);

            assertThat(responseBody).containsKey("path");
            assertThat(responseBody.get("path")).isEqualTo(servletPath);
        }

        @Test
        @DisplayName("Debería incluir timestamp en el cuerpo de respuesta")
        void shouldIncludeTimestampInResponseBody() throws IOException {
            // Given
            AuthenticationException authException = new BadCredentialsException("Invalid credentials");
            when(request.getServletPath()).thenReturn("/api/users/profile");

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            String jsonResponse = response.getContentAsString();
            Map<String, Object> responseBody = objectMapper.readValue(jsonResponse, Map.class);

            assertThat(responseBody).containsKey("timestamp");
            assertThat(responseBody.get("timestamp")).isNotNull();
            assertThat(responseBody.get("timestamp").toString()).isNotEmpty();
        }

        @Test
        @DisplayName("Debería incluir todos los campos requeridos en el cuerpo de respuesta")
        void shouldIncludeAllRequiredFieldsInResponseBody() throws IOException {
            // Given
            AuthenticationException authException = new BadCredentialsException("Invalid credentials");
            when(request.getServletPath()).thenReturn("/api/users/profile");

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            String jsonResponse = response.getContentAsString();
            Map<String, Object> responseBody = objectMapper.readValue(jsonResponse, Map.class);

            assertThat(responseBody).containsKeys("status", "error", "message", "path", "timestamp");
            assertThat(responseBody).hasSize(5);
        }
    }

    @Nested
    @DisplayName("Pruebas de Diferentes Paths")
    class DifferentPathsTests {

        @Test
        @DisplayName("Debería manejar path de usuarios")
        void shouldHandleUsersPath() throws IOException {
            // Given
            String usersPath = "/api/users";
            AuthenticationException authException = new BadCredentialsException("Invalid token");
            when(request.getServletPath()).thenReturn(usersPath);

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            String jsonResponse = response.getContentAsString();
            Map<String, Object> responseBody = objectMapper.readValue(jsonResponse, Map.class);

            assertThat(responseBody.get("path")).isEqualTo(usersPath);
        }

        @Test
        @DisplayName("Debería manejar path de admin")
        void shouldHandleAdminPath() throws IOException {
            // Given
            String adminPath = "/api/admin/dashboard";
            AuthenticationException authException = new BadCredentialsException("Invalid token");
            when(request.getServletPath()).thenReturn(adminPath);

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            String jsonResponse = response.getContentAsString();
            Map<String, Object> responseBody = objectMapper.readValue(jsonResponse, Map.class);

            assertThat(responseBody.get("path")).isEqualTo(adminPath);
        }

        @Test
        @DisplayName("Debería manejar path raíz")
        void shouldHandleRootPath() throws IOException {
            // Given
            String rootPath = "/";
            AuthenticationException authException = new BadCredentialsException("Invalid token");
            when(request.getServletPath()).thenReturn(rootPath);

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            String jsonResponse = response.getContentAsString();
            Map<String, Object> responseBody = objectMapper.readValue(jsonResponse, Map.class);

            assertThat(responseBody.get("path")).isEqualTo(rootPath);
        }

        @Test
        @DisplayName("Debería manejar path vacío")
        void shouldHandleEmptyPath() throws IOException {
            // Given
            String emptyPath = "";
            AuthenticationException authException = new BadCredentialsException("Invalid token");
            when(request.getServletPath()).thenReturn(emptyPath);

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            String jsonResponse = response.getContentAsString();
            Map<String, Object> responseBody = objectMapper.readValue(jsonResponse, Map.class);

            assertThat(responseBody.get("path")).isEqualTo(emptyPath);
        }

        @Test
        @DisplayName("Debería manejar path con parámetros")
        void shouldHandlePathWithParameters() throws IOException {
            // Given
            String pathWithParams = "/api/users/123";
            AuthenticationException authException = new BadCredentialsException("Invalid token");
            when(request.getServletPath()).thenReturn(pathWithParams);

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            String jsonResponse = response.getContentAsString();
            Map<String, Object> responseBody = objectMapper.readValue(jsonResponse, Map.class);

            assertThat(responseBody.get("path")).isEqualTo(pathWithParams);
        }
    }

    @Nested
    @DisplayName("Pruebas de Diferentes Excepciones")
    class DifferentExceptionsTests {

        @Test
        @DisplayName("Debería manejar BadCredentialsException")
        void shouldHandleBadCredentialsException() throws IOException {
            // Given
            AuthenticationException authException = new BadCredentialsException("Bad credentials");
            when(request.getServletPath()).thenReturn("/api/users/profile");

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            assertThat(response.getStatus()).isEqualTo(401);
            String jsonResponse = response.getContentAsString();
            Map<String, Object> responseBody = objectMapper.readValue(jsonResponse, Map.class);
            assertThat(responseBody.get("message")).isEqualTo("Token inválido, expirado o no proporcionado");
        }

        @Test
        @DisplayName("Debería manejar AuthenticationException genérica")
        void shouldHandleGenericAuthenticationException() throws IOException {
            // Given
            AuthenticationException authException = new AuthenticationException("Generic auth error") {};
            when(request.getServletPath()).thenReturn("/api/users/profile");

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            assertThat(response.getStatus()).isEqualTo(401);
            String jsonResponse = response.getContentAsString();
            Map<String, Object> responseBody = objectMapper.readValue(jsonResponse, Map.class);
            assertThat(responseBody.get("error")).isEqualTo("Unauthorized");
        }

        @Test
        @DisplayName("Debería retornar el mismo mensaje independientemente de la excepción")
        void shouldReturnSameMessageRegardlessOfException() throws IOException {
            // Given
            AuthenticationException exception1 = new BadCredentialsException("Error 1");
            AuthenticationException exception2 = new AuthenticationException("Error 2") {};
            when(request.getServletPath()).thenReturn("/api/test");

            // When
            jwtAuthenticationEntryPoint.commence(request, response, exception1);
            String jsonResponse1 = response.getContentAsString();
            Map<String, Object> responseBody1 = objectMapper.readValue(jsonResponse1, Map.class);

            response = new MockHttpServletResponse();
            jwtAuthenticationEntryPoint.commence(request, response, exception2);
            String jsonResponse2 = response.getContentAsString();
            Map<String, Object> responseBody2 = objectMapper.readValue(jsonResponse2, Map.class);

            // Then
            assertThat(responseBody1.get("message")).isEqualTo(responseBody2.get("message"));
            assertThat(responseBody1.get("message")).isEqualTo("Token inválido, expirado o no proporcionado");
        }
    }

    @Nested
    @DisplayName("Pruebas de Formato de Timestamp")
    class TimestampFormatTests {

        @Test
        @DisplayName("Debería incluir timestamp con formato de fecha válido")
        void shouldIncludeTimestampWithValidDateFormat() throws IOException {
            // Given
            AuthenticationException authException = new BadCredentialsException("Invalid token");
            when(request.getServletPath()).thenReturn("/api/users/profile");

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            String jsonResponse = response.getContentAsString();
            Map<String, Object> responseBody = objectMapper.readValue(jsonResponse, Map.class);
            String timestamp = responseBody.get("timestamp").toString();

            assertThat(timestamp).matches("\\d{4}-\\d{2}-\\d{2}T.*");
        }

        @Test
        @DisplayName("Debería generar timestamp único para cada llamada")
        void shouldGenerateUniqueTimestampForEachCall() throws IOException, InterruptedException {
            // Given
            AuthenticationException authException = new BadCredentialsException("Invalid token");
            when(request.getServletPath()).thenReturn("/api/users/profile");

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);
            String jsonResponse1 = response.getContentAsString();
            Map<String, Object> responseBody1 = objectMapper.readValue(jsonResponse1, Map.class);
            String timestamp1 = responseBody1.get("timestamp").toString();

            Thread.sleep(10); // Pequeña pausa para asegurar diferente timestamp

            response = new MockHttpServletResponse();
            jwtAuthenticationEntryPoint.commence(request, response, authException);
            String jsonResponse2 = response.getContentAsString();
            Map<String, Object> responseBody2 = objectMapper.readValue(jsonResponse2, Map.class);
            String timestamp2 = responseBody2.get("timestamp").toString();

            // Then
            assertThat(timestamp1).isNotEqualTo(timestamp2);
        }
    }

    @Nested
    @DisplayName("Pruebas de Integración")
    class IntegrationTests {

        @Test
        @DisplayName("Debería verificar interacción con request")
        void shouldVerifyInteractionWithRequest() throws IOException {
            // Given
            AuthenticationException authException = new BadCredentialsException("Invalid token");
            when(request.getServletPath()).thenReturn("/api/users/profile");

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            verify(request, times(1)).getServletPath();
        }

        @Test
        @DisplayName("Debería escribir respuesta completa sin errores")
        void shouldWriteCompleteResponseWithoutErrors() throws IOException {
            // Given
            AuthenticationException authException = new BadCredentialsException("Invalid token");
            when(request.getServletPath()).thenReturn("/api/users/profile");

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            assertThat(response.getStatus()).isEqualTo(401);
            assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
            assertThat(response.getContentAsString()).isNotEmpty();
        }

        @Test
        @DisplayName("Debería crear respuesta JSON serializable")
        void shouldCreateSerializableJsonResponse() throws IOException {
            // Given
            AuthenticationException authException = new BadCredentialsException("Invalid token");
            when(request.getServletPath()).thenReturn("/api/users/profile");

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            String jsonResponse = response.getContentAsString();

            // Verificar que puede ser deserializado correctamente
            Map<String, Object> responseBody = objectMapper.readValue(jsonResponse, Map.class);
            assertThat(responseBody).isNotNull();
            assertThat(responseBody).isNotEmpty();

            // Verificar que puede ser re-serializado
            String reSerialized = objectMapper.writeValueAsString(responseBody);
            assertThat(reSerialized).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Pruebas de Escenarios Realistas")
    class RealisticScenariosTests {

        @Test
        @DisplayName("Debería manejar acceso sin token a recurso protegido")
        void shouldHandleAccessWithoutTokenToProtectedResource() throws IOException {
            // Given
            AuthenticationException authException = new BadCredentialsException("No token provided");
            when(request.getServletPath()).thenReturn("/api/users/profile");

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            assertThat(response.getStatus()).isEqualTo(401);
            String jsonResponse = response.getContentAsString();
            Map<String, Object> responseBody = objectMapper.readValue(jsonResponse, Map.class);
            assertThat(responseBody.get("message")).isEqualTo("Token inválido, expirado o no proporcionado");
        }

        @Test
        @DisplayName("Debería manejar token expirado")
        void shouldHandleExpiredToken() throws IOException {
            // Given
            AuthenticationException authException = new BadCredentialsException("Token expired");
            when(request.getServletPath()).thenReturn("/api/admin/users");

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            assertThat(response.getStatus()).isEqualTo(401);
            String jsonResponse = response.getContentAsString();
            Map<String, Object> responseBody = objectMapper.readValue(jsonResponse, Map.class);
            assertThat(responseBody.get("error")).isEqualTo("Unauthorized");
        }

        @Test
        @DisplayName("Debería manejar token malformado")
        void shouldHandleMalformedToken() throws IOException {
            // Given
            AuthenticationException authException = new BadCredentialsException("Malformed token");
            when(request.getServletPath()).thenReturn("/api/products");

            // When
            jwtAuthenticationEntryPoint.commence(request, response, authException);

            // Then
            assertThat(response.getStatus()).isEqualTo(401);
            String jsonResponse = response.getContentAsString();
            Map<String, Object> responseBody = objectMapper.readValue(jsonResponse, Map.class);
            assertThat(responseBody).containsKeys("status", "error", "message", "path", "timestamp");
        }
    }
}
