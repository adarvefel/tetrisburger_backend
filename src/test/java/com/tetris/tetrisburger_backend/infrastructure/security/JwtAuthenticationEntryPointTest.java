package com.tetris.tetrisburger_backend.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.ErrorResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("Pruebas unitarias de JwtAuthenticationEntryPoint")
class JwtAuthenticationEntryPointTest {

    private JwtAuthenticationEntryPoint entryPoint;
    private ObjectMapper objectMapper;
    private AuthenticationException authException;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // soporte para LocalDateTime
        entryPoint   = new JwtAuthenticationEntryPoint(objectMapper);
        authException = mock(AuthenticationException.class);
    }

    // ─────────────────────────────────────────────────────────────
    // Código de estado y cabeceras HTTP
    // ─────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Pruebas de código de estado y cabeceras")
    class HttpHeaderTests {

        @Test
        @DisplayName("Debe retornar código de estado 401 en todos los casos")
        void shouldReturn401StatusCode() throws Exception {
            MockHttpServletRequest  request  = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            entryPoint.commence(request, response, authException);

            assertThat(response.getStatus()).isEqualTo(401);
        }

        @Test
        @DisplayName("Debe retornar Content-Type application/json")
        void shouldReturnJsonContentType() throws Exception {
            MockHttpServletRequest  request  = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            entryPoint.commence(request, response, authException);

            assertThat(response.getContentType()).contains("application/json");
        }

        @Test
        @DisplayName("Debe retornar charset UTF-8")
        void shouldReturnUtf8CharacterEncoding() throws Exception {
            MockHttpServletRequest  request  = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            entryPoint.commence(request, response, authException);

            assertThat(response.getCharacterEncoding()).isEqualToIgnoringCase("UTF-8");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Mensajes según el header Authorization
    // ─────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Pruebas de mensaje según el header Authorization")
    class MessageByAuthorizationHeaderTests {

        @Test
        @DisplayName("Debe indicar que no se proporcionó token cuando el header Authorization está ausente")
        void shouldReturnNoTokenMessageWhenAuthHeaderAbsent() throws Exception {
            MockHttpServletRequest request = buildRequest(null, "/api/users");
            MockHttpServletResponse response = new MockHttpServletResponse();

            entryPoint.commence(request, response, authException);

            ErrorResponseDTO body = parseBody(response);
            assertThat(body.message())
                    .contains("No se proporcionó token de autenticación");
        }

        @Test
        @DisplayName("Debe indicar que no se proporcionó token cuando el header Authorization está en blanco")
        void shouldReturnNoTokenMessageWhenAuthHeaderIsBlank() throws Exception {
            MockHttpServletRequest request = buildRequest("   ", "/api/users");
            MockHttpServletResponse response = new MockHttpServletResponse();

            entryPoint.commence(request, response, authException);

            ErrorResponseDTO body = parseBody(response);
            assertThat(body.message())
                    .contains("No se proporcionó token de autenticación");
        }

        @Test
        @DisplayName("Debe indicar token vacío cuando el header Authorization es una cadena vacía")
        void shouldReturnNoTokenMessageWhenAuthHeaderIsEmpty() throws Exception {
            MockHttpServletRequest request = buildRequest("", "/api/users");
            MockHttpServletResponse response = new MockHttpServletResponse();

            entryPoint.commence(request, response, authException);

            ErrorResponseDTO body = parseBody(response);
            assertThat(body.message())
                    .contains("No se proporcionó token de autenticación");
        }

        @Test
        @DisplayName("Debe indicar formato inválido cuando el header usa esquema Basic en lugar de Bearer")
        void shouldReturnInvalidFormatMessageWhenHeaderUsesBasicScheme() throws Exception {
            MockHttpServletRequest request = buildRequest("Basic dXNlcjpwYXNz", "/api/users");
            MockHttpServletResponse response = new MockHttpServletResponse();

            entryPoint.commence(request, response, authException);

            ErrorResponseDTO body = parseBody(response);
            assertThat(body.message())
                    .contains("Formato de token inválido");
        }

        @Test
        @DisplayName("Debe indicar formato inválido cuando el header contiene solo el token sin prefijo Bearer")
        void shouldReturnInvalidFormatMessageWhenHeaderHasNoBearerPrefix() throws Exception {
            MockHttpServletRequest request = buildRequest("some.jwt.token", "/api/users");
            MockHttpServletResponse response = new MockHttpServletResponse();

            entryPoint.commence(request, response, authException);

            ErrorResponseDTO body = parseBody(response);
            assertThat(body.message())
                    .contains("Formato de token inválido");
        }

        @Test
        @DisplayName("Debe indicar falta de permisos cuando el header tiene formato Bearer correcto")
        void shouldReturnNoPermissionsMessageWhenBearerFormatIsCorrect() throws Exception {
            MockHttpServletRequest request = buildRequest("Bearer some.valid.jwt.token", "/api/users");
            MockHttpServletResponse response = new MockHttpServletResponse();

            entryPoint.commence(request, response, authException);

            ErrorResponseDTO body = parseBody(response);
            assertThat(body.message())
                    .contains("No tienes permisos para acceder a este recurso");
        }

        @Test
        @DisplayName("Debe distinguir 'Bearer' sin espacio como formato inválido")
        void shouldReturnInvalidFormatWhenBearerHasNoSpace() throws Exception {
            // "Bearer" sin espacio posterior no cumple startsWith("Bearer ")
            MockHttpServletRequest request = buildRequest("Bearer", "/api/users");
            MockHttpServletResponse response = new MockHttpServletResponse();

            entryPoint.commence(request, response, authException);

            ErrorResponseDTO body = parseBody(response);
            assertThat(body.message())
                    .contains("Formato de token inválido");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Estructura del body ErrorResponseDTO
    // ─────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Pruebas de estructura del body de error")
    class ErrorBodyStructureTests {

        @Test
        @DisplayName("Debe incluir el código 401 en el campo status del body")
        void shouldInclude401StatusInBody() throws Exception {
            MockHttpServletRequest  request  = buildRequest(null, "/api/users");
            MockHttpServletResponse response = new MockHttpServletResponse();

            entryPoint.commence(request, response, authException);

            assertThat(parseBody(response).status()).isEqualTo(401);
        }

        @Test
        @DisplayName("Debe incluir 'Unauthorized' en el campo error del body")
        void shouldIncludeUnauthorizedInErrorField() throws Exception {
            MockHttpServletRequest  request  = buildRequest(null, "/api/users");
            MockHttpServletResponse response = new MockHttpServletResponse();

            entryPoint.commence(request, response, authException);

            assertThat(parseBody(response).error()).isEqualTo("Unauthorized");
        }

        @Test
        @DisplayName("Debe incluir la URI de la petición en el campo path del body")
        void shouldIncludeRequestUriInPathField() throws Exception {
            MockHttpServletRequest  request  = buildRequest(null, "/api/v1/orders/123");
            MockHttpServletResponse response = new MockHttpServletResponse();

            entryPoint.commence(request, response, authException);

            assertThat(parseBody(response).path()).isEqualTo("/api/v1/orders/123");
        }

        @Test
        @DisplayName("Debe incluir un timestamp no nulo en el campo timestamp del body")
        void shouldIncludeNonNullTimestampInBody() throws Exception {
            MockHttpServletRequest  request  = buildRequest(null, "/api/users");
            MockHttpServletResponse response = new MockHttpServletResponse();

            entryPoint.commence(request, response, authException);

            assertThat(parseBody(response).timestamp()).isNotNull();
        }

        @Test
        @DisplayName("Debe producir un body JSON deserializable como ErrorResponseDTO completo")
        void shouldProduceFullyDeserializableErrorResponseDTO() throws Exception {
            MockHttpServletRequest  request  = buildRequest("Bearer token", "/api/admin/users");
            MockHttpServletResponse response = new MockHttpServletResponse();

            entryPoint.commence(request, response, authException);

            ErrorResponseDTO body = parseBody(response);
            // Verifica todos los campos del record en un solo test de integración del objeto
            assertThat(body.status()).isEqualTo(401);
            assertThat(body.error()).isEqualTo("Unauthorized");
            assertThat(body.message()).contains("No tienes permisos");
            assertThat(body.timestamp()).isNotNull();
            assertThat(body.path()).isEqualTo("/api/admin/users");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────

    /**
     * Construye un MockHttpServletRequest con el header Authorization y la URI indicados.
     * Si authHeader es null, no agrega el header (simula ausencia real del header).
     */
    private MockHttpServletRequest buildRequest(String authHeader, String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (authHeader != null) {
            request.addHeader("Authorization", authHeader);
        }
        request.setRequestURI(uri);
        return request;
    }

    /**
     * Lee el body de la respuesta y lo deserializa como ErrorResponseDTO.
     * Usa el mismo ObjectMapper configurado con JavaTimeModule.
     */
    private ErrorResponseDTO parseBody(MockHttpServletResponse response) throws Exception {
        return objectMapper.readValue(response.getContentAsString(), ErrorResponseDTO.class);
    }
}
