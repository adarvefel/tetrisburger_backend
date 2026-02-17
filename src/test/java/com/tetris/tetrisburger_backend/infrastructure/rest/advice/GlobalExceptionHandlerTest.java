package com.tetris.tetrisburger_backend.infrastructure.rest.advice;

import com.tetris.tetrisburger_backend.domain.exception.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.ErrorResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.MessageResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
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
    private HttpServletRequest httpServletRequest;

    @Mock
    private WebRequest webRequest;

    @InjectMocks
    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        // Usar lenient() para evitar UnnecessaryStubbingException
        lenient().when(httpServletRequest.getRequestURI()).thenReturn("/api/test");
        lenient().when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    @Nested
    @DisplayName("User Exceptions")
    class UserExceptions {

        @Test
        @DisplayName("debería manejar UserAlreadyExistsException con 409 CONFLICT")
        void shouldHandleUserAlreadyExistsException() {
            // Given
            UserAlreadyExistsException ex = new UserAlreadyExistsException("El email ya está registrado");

            // When
            ResponseEntity<MessageResponseDTO> response = handler.handleUserExists(ex, webRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("El email ya está registrado");
            assertThat(response.getBody().success()).isFalse();
        }

        @Test
        @DisplayName("debería manejar UserNotFoundException con 404 NOT FOUND")
        void shouldHandleUserNotFoundException() {
            // Given
            UserNotFoundException ex = new UserNotFoundException("Usuario no encontrado con ID: 123");

            // When
            ResponseEntity<MessageResponseDTO> response = handler.handleUserNotFound(ex, webRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Usuario no encontrado con ID: 123");
            assertThat(response.getBody().success()).isFalse();
        }

        @Test
        @DisplayName("debería manejar InvalidCredentialsException con 401 UNAUTHORIZED")
        void shouldHandleInvalidCredentialsException() {
            // Given
            InvalidCredentialsException ex = new InvalidCredentialsException("Credenciales incorrectas");

            // When
            ResponseEntity<MessageResponseDTO> response = handler.handleInvalidCredentials(ex, webRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Credenciales inválidas");
            assertThat(response.getBody().success()).isFalse();
        }

        @Test
        @DisplayName("debería manejar InvalidTokenException con 400 BAD REQUEST")
        void shouldHandleInvalidTokenException() {
            // Given
            InvalidTokenException ex = new InvalidTokenException("Token expirado");

            // When
            ResponseEntity<MessageResponseDTO> response = handler.handleInvalidToken(ex, webRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Token expirado");
            assertThat(response.getBody().success()).isFalse();
        }
    }

    @Nested
    @DisplayName("Image Exceptions")
    class ImageExceptions {

        @Test
        @DisplayName("debería manejar ImageUploadException con 500 INTERNAL SERVER ERROR")
        void shouldHandleImageUploadException() {
            // Given
            ImageUploadException ex = new ImageUploadException("Error al subir a S3");

            // When
            ResponseEntity<MessageResponseDTO> response = handler.handleImageUpload(ex, webRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("No se pudo subir la imagen. Intenta de nuevo.");
            assertThat(response.getBody().success()).isFalse();
        }

        @Test
        @DisplayName("debería usar mensaje genérico para ImageUploadException sin exponer detalles internos")
        void shouldUseGenericMessageForImageUploadException() {
            // Given
            ImageUploadException ex = new ImageUploadException("AWS Access Key invalid");

            // When
            ResponseEntity<MessageResponseDTO> response = handler.handleImageUpload(ex, webRequest);

            // Then
            assertThat(response.getBody().message()).doesNotContain("AWS");
            assertThat(response.getBody().message()).isEqualTo("No se pudo subir la imagen. Intenta de nuevo.");
        }
    }

    @Nested
    @DisplayName("PQRS Exceptions")
    class PqrsExceptions {

        @Test
        @DisplayName("debería manejar PqrsAlreadyDeletedException con 400 BAD REQUEST")
        void shouldHandlePqrsAlreadyDeletedException() {
            // Given
            PqrsAlreadyDeletedException ex = new PqrsAlreadyDeletedException("PQRS ya eliminada");

            // When
            ResponseEntity<MessageResponseDTO> response = handler.handlePqrsAlreadyDeleted(ex, webRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("PQRS ya eliminada");
            assertThat(response.getBody().success()).isFalse();
        }
    }

    @Nested
    @DisplayName("Database Exceptions")
    class DatabaseExceptions {

        @Test
        @DisplayName("debería manejar DataIntegrityViolationException con mensaje de duplicado")
        void shouldHandleDataIntegrityViolationWithDuplicateMessage() {
            // Given
            DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
            Throwable cause = new RuntimeException("Duplicate entry 'test@example.com' for key 'uq_email'");
            when(ex.getMostSpecificCause()).thenReturn(cause);
            when(ex.getMessage()).thenReturn("could not execute statement");

            // When
            ResponseEntity<MessageResponseDTO> response = handler.handleDataIntegrityViolation(ex, webRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Registro duplicado: ya existe un recurso con esos datos");
            assertThat(response.getBody().success()).isFalse();
        }

        @Test
        @DisplayName("debería manejar DataIntegrityViolationException con mensaje de unique constraint")
        void shouldHandleDataIntegrityViolationWithUniqueConstraint() {
            // Given
            DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
            Throwable cause = new RuntimeException("unique constraint violated");
            when(ex.getMostSpecificCause()).thenReturn(cause);

            // When
            ResponseEntity<MessageResponseDTO> response = handler.handleDataIntegrityViolation(ex, webRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody().message()).isEqualTo("Registro duplicado: ya existe un recurso con esos datos");
        }

        @Test
        @DisplayName("debería manejar DataIntegrityViolationException con mensaje de foreign key")
        void shouldHandleDataIntegrityViolationWithForeignKey() {
            // Given
            DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
            Throwable cause = new RuntimeException("foreign key constraint fails");
            when(ex.getMostSpecificCause()).thenReturn(cause);

            // When
            ResponseEntity<MessageResponseDTO> response = handler.handleDataIntegrityViolation(ex, webRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody().message()).isEqualTo("Referencia inválida a datos relacionados");
        }

        @Test
        @DisplayName("debería manejar DataIntegrityViolationException con mensaje genérico")
        void shouldHandleDataIntegrityViolationWithGenericMessage() {
            // Given
            DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
            Throwable cause = new RuntimeException("Some other integrity error");
            when(ex.getMostSpecificCause()).thenReturn(cause);

            // When
            ResponseEntity<MessageResponseDTO> response = handler.handleDataIntegrityViolation(ex, webRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody().message()).isEqualTo("Conflicto de integridad de datos");
        }

        @Test
        @DisplayName("debería manejar DataIntegrityViolationException con cause null")
        void shouldHandleDataIntegrityViolationWithNullCause() {
            // Given
            DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
            when(ex.getMostSpecificCause()).thenReturn(null);
            when(ex.getMessage()).thenReturn("unique violation");

            // When
            ResponseEntity<MessageResponseDTO> response = handler.handleDataIntegrityViolation(ex, webRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody().message()).isEqualTo("Registro duplicado: ya existe un recurso con esos datos");
        }
    }

    @Nested
    @DisplayName("Validation Exceptions")
    class ValidationExceptions {

        @Test
        @DisplayName("debería manejar IllegalArgumentException con 400 BAD REQUEST")
        void shouldHandleIllegalArgumentException() {
            // Given
            IllegalArgumentException ex = new IllegalArgumentException("El ID debe ser positivo");

            // When
            ResponseEntity<MessageResponseDTO> response = handler.handleIllegalArgument(ex, webRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("El ID debe ser positivo");
            assertThat(response.getBody().success()).isFalse();
        }

        @Test
        @DisplayName("debería manejar MissingServletRequestParameterException con 400 BAD REQUEST")
        void shouldHandleMissingServletRequestParameterException() {
            // Given
            MissingServletRequestParameterException ex = new MissingServletRequestParameterException(
                    "page",
                    "int"
            );

            // When
            ResponseEntity<MessageResponseDTO> response = handler.handleBadRequest(ex, httpServletRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().success()).isFalse();
        }

        @Test
        @DisplayName("debería manejar HttpMediaTypeNotSupportedException con 400 BAD REQUEST")
        void shouldHandleHttpMediaTypeNotSupportedException() {
            // Given
            HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException("Content-Type not supported");

            // When
            ResponseEntity<MessageResponseDTO> response = handler.handleBadRequest(ex, httpServletRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().success()).isFalse();
        }
    }

    @Nested
    @DisplayName("Security Exceptions")
    class SecurityExceptions {

        @Test
        @DisplayName("debería manejar AccessDeniedException con 403 FORBIDDEN")
        void shouldHandleAccessDeniedException() {
            // Given
            AccessDeniedException ex = new AccessDeniedException("Access is denied");

            // When
            ResponseEntity<MessageResponseDTO> response = handler.handleAccessDenied(ex, httpServletRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Acceso denegado");
            assertThat(response.getBody().success()).isFalse();
        }

        @Test
        @DisplayName("debería usar mensaje genérico para AccessDeniedException sin exponer detalles")
        void shouldUseGenericMessageForAccessDeniedException() {
            // Given
            AccessDeniedException ex = new AccessDeniedException("User 'john' lacks ADMIN role");

            // When
            ResponseEntity<MessageResponseDTO> response = handler.handleAccessDenied(ex, httpServletRequest);

            // Then
            assertThat(response.getBody().message()).doesNotContain("john");
            assertThat(response.getBody().message()).isEqualTo("Acceso denegado");
        }
    }

    @Nested
    @DisplayName("HTTP Exceptions")
    class HttpExceptions {

        @Test
        @DisplayName("debería manejar HttpRequestMethodNotSupportedException con 405 METHOD NOT ALLOWED")
        void shouldHandleHttpRequestMethodNotSupportedException() {
            // Given
            HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST");

            // When
            ResponseEntity<MessageResponseDTO> response = handler.handleMethodNotSupported(ex, httpServletRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().success()).isFalse();
        }
    }

    @Nested
    @DisplayName("Product Category Exceptions")
    class ProductCategoryExceptions {

        @Test
        @DisplayName("debería manejar ProductCategoryNotFoundException con 404 NOT FOUND")
        void shouldHandleProductCategoryNotFoundException() {
            // Given
            ProductCategoryNotFoundException ex = new ProductCategoryNotFoundException("Categoría no encontrada con ID: 5");

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleProductCategoryNotFound(ex, webRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().status()).isEqualTo(404);
            assertThat(response.getBody().error()).isEqualTo("Not Found");
            assertThat(response.getBody().message()).isEqualTo("Categoría no encontrada con ID: 5");
            assertThat(response.getBody().timestamp()).isNotNull();
            assertThat(response.getBody().path()).isEqualTo("/api/test");
        }

        @Test
        @DisplayName("debería usar ErrorResponseDTO para ProductCategoryNotFoundException")
        void shouldUseErrorResponseDtoForProductCategoryNotFoundException() {
            // Given
            ProductCategoryNotFoundException ex = new ProductCategoryNotFoundException("Test message");

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleProductCategoryNotFound(ex, webRequest);

            // Then
            assertThat(response.getBody()).isInstanceOf(ErrorResponseDTO.class);
        }

        @Test
        @DisplayName("debería incluir path en ErrorResponseDTO")
        void shouldIncludePathInErrorResponseDto() {
            // Given
            ProductCategoryNotFoundException ex = new ProductCategoryNotFoundException("Category not found");

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleProductCategoryNotFound(ex, webRequest);

            // Then
            assertThat(response.getBody().path()).isNotNull();
            assertThat(response.getBody().path()).isEqualTo("/api/test");
        }
    }

    @Nested
    @DisplayName("JPA Entity Not Found Exceptions")
    class JpaEntityNotFoundExceptions {

        @Test
        @DisplayName("debería manejar EntityNotFoundException con mensaje de ProductCategoryEntity")
        void shouldHandleEntityNotFoundWithProductCategoryMessage() {
            // Given
            EntityNotFoundException ex = new EntityNotFoundException("Unable to find ProductCategoryEntity with id 5");

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleJpaEntityNotFound(ex, webRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().status()).isEqualTo(404);
            assertThat(response.getBody().error()).isEqualTo("Not Found");
            assertThat(response.getBody().message()).isEqualTo("Error: El producto tiene una categoría inexistente en la base de datos");
            assertThat(response.getBody().timestamp()).isNotNull();
            assertThat(response.getBody().path()).isEqualTo("/api/test");
        }

        @Test
        @DisplayName("debería manejar EntityNotFoundException con mensaje de ProductEntity")
        void shouldHandleEntityNotFoundWithProductEntityMessage() {
            // Given
            EntityNotFoundException ex = new EntityNotFoundException("Unable to find ProductEntity with id 10");

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleJpaEntityNotFound(ex, webRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Producto no encontrado");
            assertThat(response.getBody().path()).isEqualTo("/api/test");
        }

        @Test
        @DisplayName("debería manejar EntityNotFoundException con mensaje genérico")
        void shouldHandleEntityNotFoundWithGenericMessage() {
            // Given
            EntityNotFoundException ex = new EntityNotFoundException("Entity not found");

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleJpaEntityNotFound(ex, webRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Recurso no encontrado");
            assertThat(response.getBody().path()).isEqualTo("/api/test");
        }

        @Test
        @DisplayName("debería manejar EntityNotFoundException con mensaje null")
        void shouldHandleEntityNotFoundWithNullMessage() {
            // Given
            EntityNotFoundException ex = new EntityNotFoundException();

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleJpaEntityNotFound(ex, webRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Recurso no encontrado");
            assertThat(response.getBody().path()).isEqualTo("/api/test");
        }
    }

    @Nested
    @DisplayName("Fallback Exception Handler")
    class FallbackExceptionHandler {

        @Test
        @DisplayName("debería manejar Exception genérica con 500 INTERNAL SERVER ERROR")
        void shouldHandleGenericException() {
            // Given
            Exception ex = new RuntimeException("Unexpected error");

            // When
            ResponseEntity<MessageResponseDTO> response = handler.handleGenericException(ex, webRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().message()).isEqualTo("Error interno del servidor");
            assertThat(response.getBody().success()).isFalse();
        }

        @Test
        @DisplayName("debería usar mensaje genérico sin exponer detalles internos")
        void shouldUseGenericMessageWithoutExposingInternalDetails() {
            // Given
            Exception ex = new NullPointerException("Cannot invoke method on null object at line 42");

            // When
            ResponseEntity<MessageResponseDTO> response = handler.handleGenericException(ex, webRequest);

            // Then
            assertThat(response.getBody().message()).doesNotContain("null object");
            assertThat(response.getBody().message()).isEqualTo("Error interno del servidor");
        }

        @Test
        @DisplayName("debería capturar cualquier excepción no manejada específicamente")
        void shouldCatchAnyUnhandledException() {
            // Given
            Exception ex = new UnsupportedOperationException("Not implemented yet");

            // When
            ResponseEntity<MessageResponseDTO> response = handler.handleGenericException(ex, webRequest);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody().message()).isEqualTo("Error interno del servidor");
        }
    }

    @Nested
    @DisplayName("HTTP Status Codes")
    class HttpStatusCodes {

        @Test
        @DisplayName("debería retornar 400 BAD REQUEST para errores de validación")
        void shouldReturn400ForValidationErrors() {
            IllegalArgumentException ex1 = new IllegalArgumentException("Invalid input");
            assertThat(handler.handleIllegalArgument(ex1, webRequest).getStatusCode())
                    .isEqualTo(HttpStatus.BAD_REQUEST);

            InvalidTokenException ex2 = new InvalidTokenException("Invalid token");
            assertThat(handler.handleInvalidToken(ex2, webRequest).getStatusCode())
                    .isEqualTo(HttpStatus.BAD_REQUEST);

            PqrsAlreadyDeletedException ex3 = new PqrsAlreadyDeletedException("Already deleted");
            assertThat(handler.handlePqrsAlreadyDeleted(ex3, webRequest).getStatusCode())
                    .isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("debería retornar 401 UNAUTHORIZED para credenciales inválidas")
        void shouldReturn401ForInvalidCredentials() {
            InvalidCredentialsException ex = new InvalidCredentialsException("Bad credentials");
            assertThat(handler.handleInvalidCredentials(ex, webRequest).getStatusCode())
                    .isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        @DisplayName("debería retornar 403 FORBIDDEN para acceso denegado")
        void shouldReturn403ForAccessDenied() {
            AccessDeniedException ex = new AccessDeniedException("Access denied");
            assertThat(handler.handleAccessDenied(ex, httpServletRequest).getStatusCode())
                    .isEqualTo(HttpStatus.FORBIDDEN);
        }

        @Test
        @DisplayName("debería retornar 404 NOT FOUND para recursos no encontrados")
        void shouldReturn404ForNotFound() {
            UserNotFoundException ex1 = new UserNotFoundException("User not found");
            assertThat(handler.handleUserNotFound(ex1, webRequest).getStatusCode())
                    .isEqualTo(HttpStatus.NOT_FOUND);

            ProductCategoryNotFoundException ex2 = new ProductCategoryNotFoundException("Category not found");
            assertThat(handler.handleProductCategoryNotFound(ex2, webRequest).getStatusCode())
                    .isEqualTo(HttpStatus.NOT_FOUND);

            EntityNotFoundException ex3 = new EntityNotFoundException("Entity not found");
            assertThat(handler.handleJpaEntityNotFound(ex3, webRequest).getStatusCode())
                    .isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("debería retornar 405 METHOD NOT ALLOWED")
        void shouldReturn405ForMethodNotAllowed() {
            HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST");
            assertThat(handler.handleMethodNotSupported(ex, httpServletRequest).getStatusCode())
                    .isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        }

        @Test
        @DisplayName("debería retornar 409 CONFLICT para violaciones de integridad")
        void shouldReturn409ForConflict() {
            UserAlreadyExistsException ex1 = new UserAlreadyExistsException("User exists");
            assertThat(handler.handleUserExists(ex1, webRequest).getStatusCode())
                    .isEqualTo(HttpStatus.CONFLICT);

            DataIntegrityViolationException ex2 = mock(DataIntegrityViolationException.class);
            when(ex2.getMostSpecificCause()).thenReturn(new RuntimeException("duplicate"));
            assertThat(handler.handleDataIntegrityViolation(ex2, webRequest).getStatusCode())
                    .isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("debería retornar 500 INTERNAL SERVER ERROR para errores internos")
        void shouldReturn500ForInternalErrors() {
            ImageUploadException ex1 = new ImageUploadException("Upload failed");
            assertThat(handler.handleImageUpload(ex1, webRequest).getStatusCode())
                    .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

            Exception ex2 = new RuntimeException("Unexpected");
            assertThat(handler.handleGenericException(ex2, webRequest).getStatusCode())
                    .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Nested
    @DisplayName("Response DTO Types")
    class ResponseDtoTypes {

        @Test
        @DisplayName("debería usar MessageResponseDTO para la mayoría de excepciones")
        void shouldUseMessageResponseDtoForMostExceptions() {
            UserNotFoundException ex = new UserNotFoundException("Not found");
            ResponseEntity<?> response = handler.handleUserNotFound(ex, webRequest);
            assertThat(response.getBody()).isInstanceOf(MessageResponseDTO.class);
        }

        @Test
        @DisplayName("debería usar ErrorResponseDTO para ProductCategoryNotFoundException")
        void shouldUseErrorResponseDtoForProductCategoryNotFound() {
            ProductCategoryNotFoundException ex = new ProductCategoryNotFoundException("Not found");
            ResponseEntity<?> response = handler.handleProductCategoryNotFound(ex, webRequest);
            assertThat(response.getBody()).isInstanceOf(ErrorResponseDTO.class);
        }

        @Test
        @DisplayName("debería usar ErrorResponseDTO para EntityNotFoundException")
        void shouldUseErrorResponseDtoForEntityNotFound() {
            EntityNotFoundException ex = new EntityNotFoundException("Not found");
            ResponseEntity<?> response = handler.handleJpaEntityNotFound(ex, webRequest);
            assertThat(response.getBody()).isInstanceOf(ErrorResponseDTO.class);
        }
    }

    @Nested
    @DisplayName("Success Field")
    class SuccessField {

        @Test
        @DisplayName("todos los MessageResponseDTO deberían tener success=false")
        void allMessageResponsesShouldHaveSuccessFalse() {
            UserNotFoundException ex1 = new UserNotFoundException("Not found");
            assertThat(handler.handleUserNotFound(ex1, webRequest).getBody().success()).isFalse();

            IllegalArgumentException ex2 = new IllegalArgumentException("Invalid");
            assertThat(handler.handleIllegalArgument(ex2, webRequest).getBody().success()).isFalse();

            Exception ex3 = new RuntimeException("Error");
            assertThat(handler.handleGenericException(ex3, webRequest).getBody().success()).isFalse();
        }
    }

    @Nested
    @DisplayName("Path Extraction")
    class PathExtraction {

        @Test
        @DisplayName("debería extraer path desde WebRequest")
        void shouldExtractPathFromWebRequest() {
            // Given
            when(webRequest.getDescription(false)).thenReturn("uri=/api/products/123");
            ProductCategoryNotFoundException ex = new ProductCategoryNotFoundException("Not found");

            // When
            ResponseEntity<ErrorResponseDTO> response = handler.handleProductCategoryNotFound(ex, webRequest);

            // Then
            assertThat(response.getBody().path()).isEqualTo("/api/products/123");
        }

        @Test
        @DisplayName("debería incluir path en todas las respuestas ErrorResponseDTO")
        void shouldIncludePathInAllErrorResponseDto() {
            // ProductCategoryNotFoundException
            ProductCategoryNotFoundException ex1 = new ProductCategoryNotFoundException("Category not found");
            ResponseEntity<ErrorResponseDTO> response1 = handler.handleProductCategoryNotFound(ex1, webRequest);
            assertThat(response1.getBody().path()).isNotNull();

            // EntityNotFoundException
            EntityNotFoundException ex2 = new EntityNotFoundException("Entity not found");
            ResponseEntity<ErrorResponseDTO> response2 = handler.handleJpaEntityNotFound(ex2, webRequest);
            assertThat(response2.getBody().path()).isNotNull();
        }
    }
}
