package com.tetris.tetrisburger_backend.infrastructure.rest.advice;

import com.tetris.tetrisburger_backend.infrastructure.rest.dto.ErrorResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.ValidationErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de ValidationExceptionHandler")
class ValidationExceptionHandlerTest {

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private WebRequest webRequest;

    @Mock
    private ServletWebRequest servletWebRequest;

    @InjectMocks
    private ValidationExceptionHandler handler;

    @BeforeEach
    void setUp() {
        when(httpServletRequest.getRequestURI()).thenReturn("/api/test");
        when(servletWebRequest.getRequest()).thenReturn(httpServletRequest);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    @Nested
    @DisplayName("handleTypeMismatch - MethodArgumentTypeMismatchException")
    class HandleTypeMismatch {

        @Test
        @DisplayName("debería manejar tipo incorrecto en parámetro Integer")
        void shouldHandleTypeMismatchForIntegerParameter() {
            // Given
            MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                    "abc",
                    Integer.class,
                    "idUser",
                    null,
                    null
            );

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleTypeMismatch(ex, servletWebRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().status()).isEqualTo(400);
            assertThat(response.getBody().error()).isEqualTo("Bad Request");
            assertThat(response.getBody().message()).contains("idUser");
            assertThat(response.getBody().message()).contains("Integer");
            assertThat(response.getBody().message()).contains("abc");
            assertThat(response.getBody().timestamp()).isNotNull();
            assertThat(response.getBody().path()).isEqualTo("/api/test");
        }

        @Test
        @DisplayName("debería manejar tipo incorrecto en parámetro Long")
        void shouldHandleTypeMismatchForLongParameter() {
            // Given
            MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                    "invalid",
                    Long.class,
                    "productId",
                    null,
                    null
            );

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleTypeMismatch(ex, servletWebRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).contains("productId");
            assertThat(response.getBody().message()).contains("Long");
            assertThat(response.getBody().message()).contains("invalid");
        }

        @Test
        @DisplayName("debería manejar valor null en parámetro")
        void shouldHandleNullValueInParameter() {
            // Given
            MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                    null,
                    Integer.class,
                    "page",
                    null,
                    null
            );

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleTypeMismatch(ex, servletWebRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).contains("page");
            assertThat(response.getBody().message()).contains("null");
        }

        @Test
        @DisplayName("debería manejar tipo requerido null")
        void shouldHandleNullRequiredType() {
            // Given
            MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                    "test",
                    null,
                    "param",
                    null,
                    null
            );

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleTypeMismatch(ex, servletWebRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).contains("válido");
        }

        @Test
        @DisplayName("debería incluir path extraído correctamente")
        void shouldIncludeExtractedPathCorrectly() {
            // Given
            MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                    "abc",
                    Integer.class,
                    "id",
                    null,
                    null
            );

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleTypeMismatch(ex, servletWebRequest);

            // Then
            assertThat(response.getBody().path()).isEqualTo("/api/test");
        }

        @Test
        @DisplayName("debería usar ErrorResponseDTO para type mismatch")
        void shouldUseErrorResponseDtoForTypeMismatch() {
            // Given
            MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                    "abc",
                    Integer.class,
                    "id",
                    null,
                    null
            );

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleTypeMismatch(ex, servletWebRequest);

            // Then
            assertThat(response.getBody()).isInstanceOf(ErrorResponseDTO.class);
        }
    }

    @Nested
    @DisplayName("handleMissingServletRequestPart - MissingServletRequestPartException")
    class HandleMissingServletRequestPart {

        @Test
        @DisplayName("debería manejar parte faltante en multipart request")
        void shouldHandleMissingMultipartPart() {
            // Given
            MissingServletRequestPartException ex = new MissingServletRequestPartException("image");

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleMissingServletRequestPart(ex, servletWebRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().status()).isEqualTo(400);
            assertThat(response.getBody().error()).isEqualTo("Bad Request");
            assertThat(response.getBody().message()).isEqualTo("El campo 'image' es requerido");
            assertThat(response.getBody().timestamp()).isNotNull();
            assertThat(response.getBody().path()).isEqualTo("/api/test");
        }

        @Test
        @DisplayName("debería manejar diferentes nombres de partes faltantes")
        void shouldHandleDifferentMissingPartNames() {
            // Given
            MissingServletRequestPartException ex = new MissingServletRequestPartException("file");

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleMissingServletRequestPart(ex, servletWebRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).contains("file");
        }

        @Test
        @DisplayName("debería extraer path con webRequest no ServletWebRequest")
        void shouldExtractPathWithNonServletWebRequest() {
            // Given
            MissingServletRequestPartException ex = new MissingServletRequestPartException("document");

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleMissingServletRequestPart(ex, webRequest);

            // Then
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().path()).isEqualTo("/api/test");
        }
    }

    @Nested
    @DisplayName("handleValidationErrors - MethodArgumentNotValidException")
    class HandleValidationErrors {

        @Mock
        private MethodParameter methodParameter;

        @Mock
        private BindingResult bindingResult;

        @Test
        @DisplayName("debería manejar errores de validación en body")
        void shouldHandleValidationErrorsInBody() {
            // Given
            List<FieldError> fieldErrors = List.of(
                    new FieldError("user", "userName", "El nombre es requerido"),
                    new FieldError("user", "email", "El email no es válido")
            );

            when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                    methodParameter,
                    bindingResult
            );

            // When
            ResponseEntity<ValidationErrorResponseDTO> response = handler.handleValidationErrors(ex, servletWebRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().status()).isEqualTo(400);
            assertThat(response.getBody().error()).isEqualTo("Bad Request");
            assertThat(response.getBody().message()).isEqualTo("Errores de validación en los datos enviados");
            assertThat(response.getBody().timestamp()).isNotNull();
            assertThat(response.getBody().path()).isEqualTo("/api/test");
            assertThat(response.getBody().errors()).hasSize(2);
            assertThat(response.getBody().errors().get("userName")).isEqualTo("El nombre es requerido");
            assertThat(response.getBody().errors().get("email")).isEqualTo("El email no es válido");
        }

        @Test
        @DisplayName("debería manejar error de validación con mensaje null")
        void shouldHandleValidationErrorWithNullMessage() {
            // Given
            FieldError fieldError = new FieldError("user", "userName", null);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                    methodParameter,
                    bindingResult
            );

            // When
            ResponseEntity<ValidationErrorResponseDTO> response = handler.handleValidationErrors(ex, servletWebRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().errors().get("userName")).isEqualTo("Error de validación");
        }

        @Test
        @DisplayName("debería manejar múltiples errores en el mismo campo")
        void shouldHandleMultipleErrorsForSameField() {
            // Given
            List<FieldError> fieldErrors = List.of(
                    new FieldError("user", "password", "La contraseña es requerida"),
                    new FieldError("user", "password", "La contraseña debe tener al menos 8 caracteres")
            );

            when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                    methodParameter,
                    bindingResult
            );

            // When
            ResponseEntity<ValidationErrorResponseDTO> response = handler.handleValidationErrors(ex, servletWebRequest);

            // Then
            assertThat(response.getBody().errors()).hasSize(1); // Solo mantiene el primero
            assertThat(response.getBody().errors().get("password")).isEqualTo("La contraseña es requerida");
        }

        @Test
        @DisplayName("debería incluir todos los campos de ValidationErrorResponseDTO")
        void shouldIncludeAllFieldsOfValidationErrorResponse() {
            // Given
            when(bindingResult.getFieldErrors()).thenReturn(
                    List.of(new FieldError("user", "email", "Email inválido"))
            );

            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                    methodParameter,
                    bindingResult
            );

            // When
            ResponseEntity<ValidationErrorResponseDTO> response = handler.handleValidationErrors(ex, servletWebRequest);

            // Then
            ValidationErrorResponseDTO body = response.getBody();
            assertThat(body).isNotNull();
            assertThat(body.status()).isNotNull();
            assertThat(body.error()).isNotNull();
            assertThat(body.message()).isNotNull();
            assertThat(body.timestamp()).isNotNull();
            assertThat(body.path()).isNotNull();
            assertThat(body.errors()).isNotNull();
        }

        @Test
        @DisplayName("debería usar ValidationErrorResponseDTO")
        void shouldUseValidationErrorResponseDto() {
            // Given
            when(bindingResult.getFieldErrors()).thenReturn(
                    List.of(new FieldError("user", "email", "Error"))
            );

            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                    methodParameter,
                    bindingResult
            );

            // When
            ResponseEntity<ValidationErrorResponseDTO> response = handler.handleValidationErrors(ex, servletWebRequest);

            // Then
            assertThat(response.getBody()).isInstanceOf(ValidationErrorResponseDTO.class);
        }
    }

    @Nested
    @DisplayName("handleConstraintViolation - ConstraintViolationException")
    class HandleConstraintViolation {

        @Test
        @DisplayName("debería manejar violaciones de constraint en parámetros")
        void shouldHandleConstraintViolationsInParameters() {
            // Given
            Set<ConstraintViolation<?>> violations = new HashSet<>();

            ConstraintViolation<?> violation1 = createMockViolation("idUser", "El ID debe ser positivo");
            ConstraintViolation<?> violation2 = createMockViolation("email", "El email no es válido");

            violations.add(violation1);
            violations.add(violation2);

            ConstraintViolationException ex = new ConstraintViolationException(violations);

            // When
            ResponseEntity<ValidationErrorResponseDTO> response = handler.handleConstraintViolation(ex, servletWebRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().status()).isEqualTo(400);
            assertThat(response.getBody().error()).isEqualTo("Bad Request");
            assertThat(response.getBody().message()).isEqualTo("Errores de validación en los parámetros");
            assertThat(response.getBody().timestamp()).isNotNull();
            assertThat(response.getBody().path()).isEqualTo("/api/test");
            assertThat(response.getBody().errors()).hasSize(2);
            assertThat(response.getBody().errors()).containsKey("idUser");
            assertThat(response.getBody().errors()).containsKey("email");
        }

        @Test
        @DisplayName("debería manejar violación única de constraint")
        void shouldHandleSingleConstraintViolation() {
            // Given
            Set<ConstraintViolation<?>> violations = new HashSet<>();
            violations.add(createMockViolation("age", "La edad debe ser mayor a 0"));

            ConstraintViolationException ex = new ConstraintViolationException(violations);

            // When
            ResponseEntity<ValidationErrorResponseDTO> response = handler.handleConstraintViolation(ex, servletWebRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().errors()).hasSize(1);
            assertThat(response.getBody().errors().get("age")).isEqualTo("La edad debe ser mayor a 0");
        }

        @Test
        @DisplayName("debería usar ValidationErrorResponseDTO para constraint violations")
        void shouldUseValidationErrorResponseDtoForConstraintViolations() {
            // Given
            Set<ConstraintViolation<?>> violations = new HashSet<>();
            violations.add(createMockViolation("field", "Error"));

            ConstraintViolationException ex = new ConstraintViolationException(violations);

            // When
            ResponseEntity<ValidationErrorResponseDTO> response = handler.handleConstraintViolation(ex, servletWebRequest);

            // Then
            assertThat(response.getBody()).isInstanceOf(ValidationErrorResponseDTO.class);
        }

        private ConstraintViolation<?> createMockViolation(String propertyPath, String message) {
            ConstraintViolation<?> violation = mock(ConstraintViolation.class);
            Path path = mock(Path.class);
            when(path.toString()).thenReturn(propertyPath);
            when(violation.getPropertyPath()).thenReturn(path);
            when(violation.getMessage()).thenReturn(message);
            return violation;
        }
    }

    @Nested
    @DisplayName("handleBindException - BindException")
    class HandleBindExceptionTest {

        @Test
        @DisplayName("debería manejar errores de binding en query/form parameters")
        void shouldHandleBindingErrorsInQueryOrFormParameters() {
            // Given
            BindException ex = new BindException(new Object(), "user");
            ex.addError(new FieldError("user", "page", "El número de página no es válido"));
            ex.addError(new FieldError("user", "size", "El tamaño debe ser positivo"));

            // When
            ResponseEntity<ValidationErrorResponseDTO> response = handler.handleBindException(ex, servletWebRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().status()).isEqualTo(400);
            assertThat(response.getBody().error()).isEqualTo("Bad Request");
            assertThat(response.getBody().message()).isEqualTo("Errores de validación en el binding");
            assertThat(response.getBody().timestamp()).isNotNull();
            assertThat(response.getBody().path()).isEqualTo("/api/test");
            assertThat(response.getBody().errors()).hasSize(2);
            assertThat(response.getBody().errors().get("page")).isEqualTo("El número de página no es válido");
            assertThat(response.getBody().errors().get("size")).isEqualTo("El tamaño debe ser positivo");
        }

        @Test
        @DisplayName("debería manejar error de binding con mensaje null")
        void shouldHandleBindingErrorWithNullMessage() {
            // Given
            BindException ex = new BindException(new Object(), "form");
            ex.addError(new FieldError("form", "field", null));

            // When
            ResponseEntity<ValidationErrorResponseDTO> response = handler.handleBindException(ex, servletWebRequest);

            // Then
            assertThat(response.getBody().errors().get("field")).isEqualTo("Error de validación");
        }

        @Test
        @DisplayName("debería usar ValidationErrorResponseDTO para bind exceptions")
        void shouldUseValidationErrorResponseDtoForBindExceptions() {
            // Given
            BindException ex = new BindException(new Object(), "test");
            ex.addError(new FieldError("test", "field", "Error"));

            // When
            ResponseEntity<ValidationErrorResponseDTO> response = handler.handleBindException(ex, servletWebRequest);

            // Then
            assertThat(response.getBody()).isInstanceOf(ValidationErrorResponseDTO.class);
        }
    }

    @Nested
    @DisplayName("handleMaxUploadSize - MaxUploadSizeExceededException")
    class HandleMaxUploadSize {

        @Test
        @DisplayName("debería manejar archivo que excede el tamaño máximo")
        void shouldHandleFileThatExceedsMaxSize() {
            // Given
            MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(5 * 1024 * 1024);

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleMaxUploadSize(ex, servletWebRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().status()).isEqualTo(413);
            assertThat(response.getBody().error()).isEqualTo("Payload Too Large");
            assertThat(response.getBody().message()).isEqualTo("El archivo excede el tamaño máximo permitido (5MB)");
            assertThat(response.getBody().timestamp()).isNotNull();
            assertThat(response.getBody().path()).isEqualTo("/api/test");
        }

        @Test
        @DisplayName("debería retornar código de estado 413")
        void shouldReturn413StatusCode() {
            // Given
            MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(10 * 1024 * 1024);

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleMaxUploadSize(ex, servletWebRequest);

            // Then
            assertThat(response.getStatusCode().value()).isEqualTo(413);
        }

        @Test
        @DisplayName("debería usar ErrorResponseDTO para max upload size")
        void shouldUseErrorResponseDtoForMaxUploadSize() {
            // Given
            MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(5 * 1024 * 1024);

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleMaxUploadSize(ex, servletWebRequest);

            // Then
            assertThat(response.getBody()).isInstanceOf(ErrorResponseDTO.class);
        }
    }

    @Nested
    @DisplayName("Extracción de path")
    class PathExtraction {

        @Test
        @DisplayName("debería extraer path desde ServletWebRequest")
        void shouldExtractPathFromServletWebRequest() {
            // Given
            when(httpServletRequest.getRequestURI()).thenReturn("/api/users/123");
            MissingServletRequestPartException ex = new MissingServletRequestPartException("file");

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleMissingServletRequestPart(ex, servletWebRequest);

            // Then
            assertThat(response.getBody().path()).isEqualTo("/api/users/123");
        }

        @Test
        @DisplayName("debería extraer path desde WebRequest con formato uri=")
        void shouldExtractPathFromWebRequestWithUriFormat() {
            // Given
            when(webRequest.getDescription(false)).thenReturn("uri=/api/products");
            MissingServletRequestPartException ex = new MissingServletRequestPartException("image");

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleMissingServletRequestPart(ex, webRequest);

            // Then
            assertThat(response.getBody().path()).isEqualTo("/api/products");
        }
    }

    @Nested
    @DisplayName("Estructura de respuestas")
    class ResponseStructure {

        @Test
        @DisplayName("ErrorResponseDTO debería tener estructura completa")
        void errorResponseDtoShouldHaveCompleteStructure() {
            // Given
            MissingServletRequestPartException ex = new MissingServletRequestPartException("file");

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleMissingServletRequestPart(ex, servletWebRequest);

            // Then
            ErrorResponseDTO body = response.getBody();
            assertThat(body).isNotNull();
            assertThat(body.status()).isNotNull().isPositive();
            assertThat(body.error()).isNotNull().isNotBlank();
            assertThat(body.message()).isNotNull().isNotBlank();
            assertThat(body.timestamp()).isNotNull();
            assertThat(body.path()).isNotNull().isNotBlank();
        }

        @Test
        @DisplayName("ValidationErrorResponseDTO debería tener estructura completa")
        void validationErrorResponseDtoShouldHaveCompleteStructure() {
            // Given
            BindException ex = new BindException(new Object(), "test");
            ex.addError(new FieldError("test", "field", "Error"));

            // When
            ResponseEntity<ValidationErrorResponseDTO> response = handler.handleBindException(ex, servletWebRequest);

            // Then
            ValidationErrorResponseDTO body = response.getBody();
            assertThat(body).isNotNull();
            assertThat(body.status()).isNotNull().isPositive();
            assertThat(body.error()).isNotNull().isNotBlank();
            assertThat(body.message()).isNotNull().isNotBlank();
            assertThat(body.timestamp()).isNotNull();
            assertThat(body.path()).isNotNull().isNotBlank();
            assertThat(body.errors()).isNotNull().isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Códigos de estado HTTP")
    class HttpStatusCodes {

        @Test
        @DisplayName("todos los handlers de validación deberían retornar 400 BAD REQUEST")
        void allValidationHandlersShouldReturn400() {
            // Type mismatch
            MethodArgumentTypeMismatchException typeMismatch = new MethodArgumentTypeMismatchException(
                    "abc", Integer.class, "id", null, null
            );
            assertThat(handler.handleTypeMismatch(typeMismatch, servletWebRequest).getStatusCode())
                    .isEqualTo(HttpStatus.BAD_REQUEST);

            // Missing part
            MissingServletRequestPartException missingPart = new MissingServletRequestPartException("file");
            assertThat(handler.handleMissingServletRequestPart(missingPart, servletWebRequest).getStatusCode())
                    .isEqualTo(HttpStatus.BAD_REQUEST);

            // Bind exception
            BindException bindEx = new BindException(new Object(), "test");
            bindEx.addError(new FieldError("test", "field", "Error"));
            assertThat(handler.handleBindException(bindEx, servletWebRequest).getStatusCode())
                    .isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("handler de tamaño máximo debería retornar 413 PAYLOAD TOO LARGE")
        void maxSizeHandlerShouldReturn413() {
            // Given
            MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(5 * 1024 * 1024);

            // When / Then
            assertThat(handler.handleMaxUploadSize(ex, servletWebRequest).getStatusCode())
                    .isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
        }
    }

    @Nested
    @DisplayName("Mensajes de error")
    class ErrorMessages {

        @Test
        @DisplayName("mensaje de validación en body debería ser descriptivo")
        void validationInBodyMessageShouldBeDescriptive() {
            // Given
            BindException ex = new BindException(new Object(), "user");
            ex.addError(new FieldError("user", "email", "Error"));

            // When
            ResponseEntity<ValidationErrorResponseDTO> response = handler.handleBindException(ex, servletWebRequest);

            // Then
            assertThat(response.getBody().message()).isEqualTo("Errores de validación en el binding");
        }

        @Test
        @DisplayName("mensaje de constraint violation debería ser descriptivo")
        void constraintViolationMessageShouldBeDescriptive() {
            // Given
            Set<ConstraintViolation<?>> violations = new HashSet<>();
            ConstraintViolation<?> violation = mock(ConstraintViolation.class);
            Path path = mock(Path.class);
            when(path.toString()).thenReturn("id");
            when(violation.getPropertyPath()).thenReturn(path);
            when(violation.getMessage()).thenReturn("Error");
            violations.add(violation);

            ConstraintViolationException ex = new ConstraintViolationException(violations);

            // When
            ResponseEntity<ValidationErrorResponseDTO> response = handler.handleConstraintViolation(ex, servletWebRequest);

            // Then
            assertThat(response.getBody().message()).isEqualTo("Errores de validación en los parámetros");
        }

        @Test
        @DisplayName("mensaje de type mismatch debería incluir detalles del parámetro")
        void typeMismatchMessageShouldIncludeParameterDetails() {
            // Given
            MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                    "abc", Integer.class, "userId", null, null
            );

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleTypeMismatch(ex, servletWebRequest);

            // Then
            assertThat(response.getBody().message()).contains("userId", "Integer", "abc");
        }
    }

    @Nested
    @DisplayName("Tipos de DTO de respuesta")
    class ResponseDtoTypes {

        @Test
        @DisplayName("debería usar ErrorResponseDTO para errores simples")
        void shouldUseErrorResponseDtoForSimpleErrors() {
            MissingServletRequestPartException ex = new MissingServletRequestPartException("file");
            ResponseEntity<?> response = handler.handleMissingServletRequestPart(ex, servletWebRequest);
            assertThat(response.getBody()).isInstanceOf(ErrorResponseDTO.class);
        }

        @Test
        @DisplayName("debería usar ValidationErrorResponseDTO para errores de validación múltiples")
        void shouldUseValidationErrorResponseDtoForMultipleValidationErrors() {
            BindException ex = new BindException(new Object(), "test");
            ex.addError(new FieldError("test", "field", "Error"));
            ResponseEntity<?> response = handler.handleBindException(ex, servletWebRequest);
            assertThat(response.getBody()).isInstanceOf(ValidationErrorResponseDTO.class);
        }
    }
}
