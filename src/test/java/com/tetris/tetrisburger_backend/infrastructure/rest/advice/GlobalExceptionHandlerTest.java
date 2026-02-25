package com.tetris.tetrisburger_backend.infrastructure.rest.advice;

import com.tetris.tetrisburger_backend.domain.exception.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.ErrorResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.MessageResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    @Mock
    private WebRequest webRequest; // solo para handlers con ErrorResponseDTO

    @InjectMocks
    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        lenient().when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    // ── User Exceptions ───────────────────────────────────────────────────

    @Nested
    @DisplayName("User Exceptions")
    class UserExceptions {

        @Test
        @DisplayName("debería manejar UserAlreadyExistsException con 409 CONFLICT")
        void shouldHandleUserAlreadyExistsException() {
            UserAlreadyExistsException ex = new UserAlreadyExistsException("El email ya está registrado");

            ResponseEntity<MessageResponseDTO> response = handler.handleUserExists(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Usuario ya existe"); // ← mensaje fijo del handler
            assertThat(response.getBody().success()).isFalse();
        }

        @Test
        @DisplayName("debería manejar UserNotFoundException con 404 NOT FOUND")
        void shouldHandleUserNotFoundException() {
            UserNotFoundException ex = new UserNotFoundException("Usuario no encontrado con ID: 123");

            ResponseEntity<MessageResponseDTO> response = handler.handleUserNotFound(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Usuario no encontrado"); // ← mensaje fijo del handler
            assertThat(response.getBody().success()).isFalse();
        }

        @Test
        @DisplayName("debería manejar InvalidCredentialsException con 401 UNAUTHORIZED")
        void shouldHandleInvalidCredentialsException() {
            InvalidCredentialsException ex = new InvalidCredentialsException("Credenciales incorrectas");

            ResponseEntity<MessageResponseDTO> response = handler.handleInvalidCredentials(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Credenciales inválidas");
            assertThat(response.getBody().success()).isFalse();
        }

        @Test
        @DisplayName("debería manejar InvalidTokenException con 400 BAD REQUEST")
        void shouldHandleInvalidTokenException() {
            InvalidTokenException ex = new InvalidTokenException("Token expirado");

            ResponseEntity<MessageResponseDTO> response = handler.handleInvalidToken(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Token inválido"); // ← mensaje fijo del handler
            assertThat(response.getBody().success()).isFalse();
        }
    }

    // ── Image Exceptions ──────────────────────────────────────────────────

    @Nested
    @DisplayName("Image Exceptions")
    class ImageExceptions {

        @Test
        @DisplayName("debería manejar ImageUploadException con 500 INTERNAL SERVER ERROR")
        void shouldHandleImageUploadException() {
            ImageUploadException ex = new ImageUploadException("Error al subir a S3");

            ResponseEntity<MessageResponseDTO> response = handler.handleImageUpload(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("No se pudo subir la imagen. Intenta de nuevo.");
            assertThat(response.getBody().success()).isFalse();
        }

        @Test
        @DisplayName("debería usar mensaje genérico para ImageUploadException sin exponer detalles internos")
        void shouldUseGenericMessageForImageUploadException() {
            ImageUploadException ex = new ImageUploadException("AWS Access Key invalid");

            ResponseEntity<MessageResponseDTO> response = handler.handleImageUpload(ex);

            assertThat(response.getBody().message()).doesNotContain("AWS");
            assertThat(response.getBody().message()).isEqualTo("No se pudo subir la imagen. Intenta de nuevo.");
        }
    }

    // ── PQRS Exceptions ───────────────────────────────────────────────────

    @Nested
    @DisplayName("PQRS Exceptions")
    class PqrsExceptions {

        @Test
        @DisplayName("debería manejar PqrsAlreadyDeletedException con 400 BAD REQUEST")
        void shouldHandlePqrsAlreadyDeletedException() {
            PqrsAlreadyDeletedException ex = new PqrsAlreadyDeletedException("PQRS ya eliminada");

            ResponseEntity<MessageResponseDTO> response = handler.handlePqrsAlreadyDeleted(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("PQRS ya eliminada");
            assertThat(response.getBody().success()).isFalse();
        }
    }

    // ── Database Exceptions ───────────────────────────────────────────────

    @Nested
    @DisplayName("Database Exceptions")
    class DatabaseExceptions {

        @Test
        @DisplayName("debería manejar DataIntegrityViolationException con mensaje de duplicado")
        void shouldHandleDataIntegrityViolationWithDuplicateMessage() {
            DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
            Throwable cause = new RuntimeException("Duplicate entry 'test@example.com' for key 'uq_email'");
            when(ex.getMostSpecificCause()).thenReturn(cause);
            when(ex.getMessage()).thenReturn("could not execute statement");

            ResponseEntity<MessageResponseDTO> response = handler.handleDataIntegrityViolation(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Registro duplicado: ya existe un recurso con esos datos");
            assertThat(response.getBody().success()).isFalse();
        }

        @Test
        @DisplayName("debería manejar DataIntegrityViolationException con unique constraint")
        void shouldHandleDataIntegrityViolationWithUniqueConstraint() {
            DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
            when(ex.getMostSpecificCause()).thenReturn(new RuntimeException("unique constraint violated"));

            ResponseEntity<MessageResponseDTO> response = handler.handleDataIntegrityViolation(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody().message()).isEqualTo("Registro duplicado: ya existe un recurso con esos datos");
        }

        @Test
        @DisplayName("debería manejar DataIntegrityViolationException con foreign key")
        void shouldHandleDataIntegrityViolationWithForeignKey() {
            DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
            when(ex.getMostSpecificCause()).thenReturn(new RuntimeException("foreign key constraint fails"));

            ResponseEntity<MessageResponseDTO> response = handler.handleDataIntegrityViolation(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody().message()).isEqualTo("Referencia inválida a datos relacionados");
        }

        @Test
        @DisplayName("debería manejar DataIntegrityViolationException con mensaje genérico")
        void shouldHandleDataIntegrityViolationWithGenericMessage() {
            DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
            when(ex.getMostSpecificCause()).thenReturn(new RuntimeException("Some other integrity error"));

            ResponseEntity<MessageResponseDTO> response = handler.handleDataIntegrityViolation(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody().message()).isEqualTo("Conflicto de integridad de datos");
        }

        @Test
        @DisplayName("debería manejar DataIntegrityViolationException con cause null")
        void shouldHandleDataIntegrityViolationWithNullCause() {
            DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
            when(ex.getMostSpecificCause()).thenReturn(null);
            when(ex.getMessage()).thenReturn("unique violation");

            ResponseEntity<MessageResponseDTO> response = handler.handleDataIntegrityViolation(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody().message()).isEqualTo("Registro duplicado: ya existe un recurso con esos datos");
        }
    }

    // ── Validation Exceptions ─────────────────────────────────────────────

    @Nested
    @DisplayName("Validation Exceptions")
    class ValidationExceptions {

        @Test
        @DisplayName("debería manejar IllegalArgumentException con 400 BAD REQUEST")
        void shouldHandleIllegalArgumentException() {
            IllegalArgumentException ex = new IllegalArgumentException("El ID debe ser positivo");

            ResponseEntity<MessageResponseDTO> response = handler.handleIllegalArgument(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Argumento inválido"); // ← mensaje fijo del handler
            assertThat(response.getBody().success()).isFalse();
        }

        @Test
        @DisplayName("debería manejar MissingServletRequestParameterException con 400 BAD REQUEST")
        void shouldHandleMissingServletRequestParameterException() {
            MissingServletRequestParameterException ex =
                    new MissingServletRequestParameterException("page", "int");

            ResponseEntity<MessageResponseDTO> response = handler.handleBadRequest(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Mala solicitud de los datos");
            assertThat(response.getBody().success()).isFalse();
        }

        @Test
        @DisplayName("debería manejar HttpMediaTypeNotSupportedException con 400 BAD REQUEST")
        void shouldHandleHttpMediaTypeNotSupportedException() {
            HttpMediaTypeNotSupportedException ex =
                    new HttpMediaTypeNotSupportedException("Content-Type not supported");

            ResponseEntity<MessageResponseDTO> response = handler.handleBadRequest(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Mala solicitud de los datos");
            assertThat(response.getBody().success()).isFalse();
        }
    }

    // ── Security Exceptions ───────────────────────────────────────────────

    @Nested
    @DisplayName("Security Exceptions")
    class SecurityExceptions {

        @Test
        @DisplayName("debería manejar AccessDeniedException con 403 FORBIDDEN")
        void shouldHandleAccessDeniedException() {
            AccessDeniedException ex = new AccessDeniedException("Access is denied");

            ResponseEntity<MessageResponseDTO> response = handler.handleAccessDenied(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Acceso denegado");
            assertThat(response.getBody().success()).isFalse();
        }

        @Test
        @DisplayName("debería usar mensaje genérico para AccessDeniedException sin exponer detalles")
        void shouldUseGenericMessageForAccessDeniedException() {
            AccessDeniedException ex = new AccessDeniedException("User 'john' lacks ADMIN role");

            ResponseEntity<MessageResponseDTO> response = handler.handleAccessDenied(ex);

            assertThat(response.getBody().message()).doesNotContain("john");
            assertThat(response.getBody().message()).isEqualTo("Acceso denegado");
        }
    }

    // ── HTTP Exceptions ───────────────────────────────────────────────────

    @Nested
    @DisplayName("HTTP Exceptions")
    class HttpExceptions {

        @Test
        @DisplayName("debería manejar HttpRequestMethodNotSupportedException con 405 METHOD NOT ALLOWED")
        void shouldHandleHttpRequestMethodNotSupportedException() {
            HttpRequestMethodNotSupportedException ex =
                    new HttpRequestMethodNotSupportedException("POST");

            ResponseEntity<MessageResponseDTO> response = handler.handleMethodNotSupported(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Método no permitido");
            assertThat(response.getBody().success()).isFalse();
        }
    }

    // ── Product Category Exceptions ───────────────────────────────────────

    @Nested
    @DisplayName("Product Category Exceptions")
    class ProductCategoryExceptions {

        @Test
        @DisplayName("debería manejar ProductCategoryNotFoundException con 404 NOT FOUND")
        void shouldHandleProductCategoryNotFoundException() {
            ProductCategoryNotFoundException ex =
                    new ProductCategoryNotFoundException("Categoría no encontrada con ID: 5");

            ResponseEntity<ErrorResponseDTO> response =
                    handler.handleProductCategoryNotFound(ex, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().status()).isEqualTo(404);
            assertThat(response.getBody().error()).isEqualTo("Not Found");
            assertThat(response.getBody().message()).isEqualTo("Categoría no encontrada"); // ← mensaje fijo del handler
            assertThat(response.getBody().timestamp()).isNotNull();
            assertThat(response.getBody().path()).isEqualTo("/api/test");
        }

        @Test
        @DisplayName("debería usar ErrorResponseDTO para ProductCategoryNotFoundException")
        void shouldUseErrorResponseDtoForProductCategoryNotFoundException() {
            ProductCategoryNotFoundException ex =
                    new ProductCategoryNotFoundException("Test message");

            ResponseEntity<ErrorResponseDTO> response =
                    handler.handleProductCategoryNotFound(ex, webRequest);

            assertThat(response.getBody()).isInstanceOf(ErrorResponseDTO.class);
        }

        @Test
        @DisplayName("debería incluir path en ErrorResponseDTO")
        void shouldIncludePathInErrorResponseDto() {
            ProductCategoryNotFoundException ex =
                    new ProductCategoryNotFoundException("Category not found");

            ResponseEntity<ErrorResponseDTO> response =
                    handler.handleProductCategoryNotFound(ex, webRequest);

            assertThat(response.getBody().path()).isNotNull();
            assertThat(response.getBody().path()).isEqualTo("/api/test");
        }
    }

    // ── JPA Entity Not Found ──────────────────────────────────────────────

    @Nested
    @DisplayName("JPA Entity Not Found Exceptions")
    class JpaEntityNotFoundExceptions {

        @Test
        @DisplayName("debería manejar EntityNotFoundException con mensaje de ProductCategoryEntity")
        void shouldHandleEntityNotFoundWithProductCategoryMessage() {
            EntityNotFoundException ex =
                    new EntityNotFoundException("Unable to find ProductCategoryEntity with id 5");

            ResponseEntity<ErrorResponseDTO> response =
                    handler.handleJpaEntityNotFound(ex, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody().status()).isEqualTo(404);
            assertThat(response.getBody().error()).isEqualTo("Not Found");
            assertThat(response.getBody().message())
                    .isEqualTo("Error: El producto tiene una categoría inexistente en la base de datos");
            assertThat(response.getBody().path()).isEqualTo("/api/test");
        }

        @Test
        @DisplayName("debería manejar EntityNotFoundException con mensaje de ProductEntity")
        void shouldHandleEntityNotFoundWithProductEntityMessage() {
            EntityNotFoundException ex =
                    new EntityNotFoundException("Unable to find ProductEntity with id 10");

            ResponseEntity<ErrorResponseDTO> response =
                    handler.handleJpaEntityNotFound(ex, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody().message()).isEqualTo("Producto no encontrado");
            assertThat(response.getBody().path()).isEqualTo("/api/test");
        }

        @Test
        @DisplayName("debería manejar EntityNotFoundException con mensaje genérico")
        void shouldHandleEntityNotFoundWithGenericMessage() {
            EntityNotFoundException ex = new EntityNotFoundException("Entity not found");

            ResponseEntity<ErrorResponseDTO> response =
                    handler.handleJpaEntityNotFound(ex, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody().message()).isEqualTo("Recurso no encontrado");
        }

        @Test
        @DisplayName("debería manejar EntityNotFoundException con mensaje null")
        void shouldHandleEntityNotFoundWithNullMessage() {
            EntityNotFoundException ex = new EntityNotFoundException();

            ResponseEntity<ErrorResponseDTO> response =
                    handler.handleJpaEntityNotFound(ex, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody().message()).isEqualTo("Recurso no encontrado");
        }
    }

    // ── Fallback ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Fallback Exception Handler")
    class FallbackExceptionHandler {

        @Test
        @DisplayName("debería manejar Exception genérica con 500 INTERNAL SERVER ERROR")
        void shouldHandleGenericException() {
            Exception ex = new RuntimeException("Unexpected error");

            ResponseEntity<MessageResponseDTO> response = handler.handleGenericException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Error interno del servidor");
            assertThat(response.getBody().success()).isFalse();
        }

        @Test
        @DisplayName("debería usar mensaje genérico sin exponer detalles internos")
        void shouldUseGenericMessageWithoutExposingInternalDetails() {
            Exception ex = new NullPointerException("Cannot invoke method on null object at line 42");

            ResponseEntity<MessageResponseDTO> response = handler.handleGenericException(ex);

            assertThat(response.getBody().message()).doesNotContain("null object");
            assertThat(response.getBody().message()).isEqualTo("Error interno del servidor");
        }

        @Test
        @DisplayName("debería capturar cualquier excepción no manejada específicamente")
        void shouldCatchAnyUnhandledException() {
            Exception ex = new UnsupportedOperationException("Not implemented yet");

            ResponseEntity<MessageResponseDTO> response = handler.handleGenericException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody().message()).isEqualTo("Error interno del servidor");
        }
    }

    // ── HTTP Status Codes ─────────────────────────────────────────────────

    @Nested
    @DisplayName("HTTP Status Codes")
    class HttpStatusCodes {

        @Test
        @DisplayName("debería retornar 400 BAD REQUEST para errores de validación")
        void shouldReturn400ForValidationErrors() {
            assertThat(handler.handleIllegalArgument(
                    new IllegalArgumentException("Invalid input")).getStatusCode())
                    .isEqualTo(HttpStatus.BAD_REQUEST);

            assertThat(handler.handleInvalidToken(
                    new InvalidTokenException("Invalid token")).getStatusCode())
                    .isEqualTo(HttpStatus.BAD_REQUEST);

            assertThat(handler.handlePqrsAlreadyDeleted(
                    new PqrsAlreadyDeletedException("Already deleted")).getStatusCode())
                    .isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("debería retornar 401 UNAUTHORIZED para credenciales inválidas")
        void shouldReturn401ForInvalidCredentials() {
            assertThat(handler.handleInvalidCredentials(
                    new InvalidCredentialsException("Bad credentials")).getStatusCode())
                    .isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("debería retornar 403 FORBIDDEN para acceso denegado")
        void shouldReturn403ForAccessDenied() {
            assertThat(handler.handleAccessDenied(
                    new AccessDeniedException("Access denied")).getStatusCode())
                    .isEqualTo(HttpStatus.FORBIDDEN);
        }

        @Test
        @DisplayName("debería retornar 404 NOT FOUND para recursos no encontrados")
        void shouldReturn404ForNotFound() {
            assertThat(handler.handleUserNotFound(
                    new UserNotFoundException("User not found")).getStatusCode())
                    .isEqualTo(HttpStatus.NOT_FOUND);

            assertThat(handler.handleProductCategoryNotFound(
                    new ProductCategoryNotFoundException("Category not found"), webRequest).getStatusCode())
                    .isEqualTo(HttpStatus.NOT_FOUND);

            assertThat(handler.handleJpaEntityNotFound(
                    new EntityNotFoundException("Entity not found"), webRequest).getStatusCode())
                    .isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("debería retornar 405 METHOD NOT ALLOWED")
        void shouldReturn405ForMethodNotAllowed() {
            assertThat(handler.handleMethodNotSupported(
                    new HttpRequestMethodNotSupportedException("POST")).getStatusCode())
                    .isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        }

        @Test
        @DisplayName("debería retornar 409 CONFLICT para violaciones de integridad")
        void shouldReturn409ForConflict() {
            assertThat(handler.handleUserExists(
                    new UserAlreadyExistsException("User exists")).getStatusCode())
                    .isEqualTo(HttpStatus.CONFLICT);

            DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
            when(ex.getMostSpecificCause()).thenReturn(new RuntimeException("duplicate"));
            assertThat(handler.handleDataIntegrityViolation(ex).getStatusCode())
                    .isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("debería retornar 500 INTERNAL SERVER ERROR para errores internos")
        void shouldReturn500ForInternalErrors() {
            assertThat(handler.handleImageUpload(
                    new ImageUploadException("Upload failed")).getStatusCode())
                    .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

            assertThat(handler.handleGenericException(
                    new RuntimeException("Unexpected")).getStatusCode())
                    .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ── Response DTO Types ────────────────────────────────────────────────

    @Nested
    @DisplayName("Response DTO Types")
    class ResponseDtoTypes {

        @Test
        @DisplayName("debería usar MessageResponseDTO para la mayoría de excepciones")
        void shouldUseMessageResponseDtoForMostExceptions() {
            ResponseEntity<?> response = handler.handleUserNotFound(
                    new UserNotFoundException("Not found"));
            assertThat(response.getBody()).isInstanceOf(MessageResponseDTO.class);
        }

        @Test
        @DisplayName("debería usar ErrorResponseDTO para ProductCategoryNotFoundException")
        void shouldUseErrorResponseDtoForProductCategoryNotFound() {
            ResponseEntity<?> response = handler.handleProductCategoryNotFound(
                    new ProductCategoryNotFoundException("Not found"), webRequest);
            assertThat(response.getBody()).isInstanceOf(ErrorResponseDTO.class);
        }

        @Test
        @DisplayName("debería usar ErrorResponseDTO para EntityNotFoundException")
        void shouldUseErrorResponseDtoForEntityNotFound() {
            ResponseEntity<?> response = handler.handleJpaEntityNotFound(
                    new EntityNotFoundException("Not found"), webRequest);
            assertThat(response.getBody()).isInstanceOf(ErrorResponseDTO.class);
        }
    }

    // ── Success Field ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("Success Field")
    class SuccessField {

        @Test
        @DisplayName("todos los MessageResponseDTO deberían tener success=false")
        void allMessageResponsesShouldHaveSuccessFalse() {
            assertThat(handler.handleUserNotFound(
                    new UserNotFoundException("Not found")).getBody().success()).isFalse();
            assertThat(handler.handleIllegalArgument(
                    new IllegalArgumentException("Invalid")).getBody().success()).isFalse();
            assertThat(handler.handleGenericException(
                    new RuntimeException("Error")).getBody().success()).isFalse();
        }
    }

    // ── Path Extraction ───────────────────────────────────────────────────

    @Nested
    @DisplayName("Path Extraction")
    class PathExtraction {

        @Test
        @DisplayName("debería extraer path desde WebRequest")
        void shouldExtractPathFromWebRequest() {
            when(webRequest.getDescription(false)).thenReturn("uri=/api/products/123");

            ResponseEntity<ErrorResponseDTO> response = handler.handleProductCategoryNotFound(
                    new ProductCategoryNotFoundException("Not found"), webRequest);

            assertThat(response.getBody().path()).isEqualTo("/api/products/123");
        }

        @Test
        @DisplayName("debería incluir path en todas las respuestas ErrorResponseDTO")
        void shouldIncludePathInAllErrorResponseDto() {
            ResponseEntity<ErrorResponseDTO> response = handler.handleProductCategoryNotFound(
                    new ProductCategoryNotFoundException("Not found"), webRequest);

            assertThat(response.getBody().path()).isNotNull();
        }
    }
}
