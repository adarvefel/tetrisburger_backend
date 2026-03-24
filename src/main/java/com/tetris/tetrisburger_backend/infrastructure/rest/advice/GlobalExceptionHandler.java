package com.tetris.tetrisburger_backend.infrastructure.rest.advice;

import com.tetris.tetrisburger_backend.domain.exception.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.ErrorResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.MessageResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
@Order(100)
public class GlobalExceptionHandler {

    // ========================================
    // MÉTODOS AUXILIARES
    // ========================================

    private String extractPath(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            return ((ServletWebRequest) request).getRequest().getRequestURI();
        }
        return request.getDescription(false).replace("uri=", "");
    }

    private ResponseEntity<MessageResponseDTO> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new MessageResponseDTO(message, false));
    }

    private ResponseEntity<ErrorResponseDTO> buildErrorResponseDTO(
            HttpStatus status, String message, WebRequest request) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                status.value(),
                status.getReasonPhrase(),
                message,
                LocalDateTime.now(),
                extractPath(request)
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    // ========================================
    // USER EXCEPTIONS
    // ========================================

    // 409 CONFLICT — reglas de negocio/únicos
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<MessageResponseDTO> handleUserExists(UserAlreadyExistsException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Usuario ya existe");
    }

    // 404 NOT FOUND — recursos no encontrados
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<MessageResponseDTO> handleUserNotFound(UserNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Usuario no encontrado");
    }

    // 401 UNAUTHORIZED — credenciales inválidas
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<MessageResponseDTO> handleInvalidCredentials(InvalidCredentialsException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
    }

    // 400 BAD REQUEST — token inválido
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<MessageResponseDTO> handleInvalidToken(InvalidTokenException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Token inválido");
    }

    // ========================================
    // RECAPTCHA EXCEPTIONS
    // ========================================

    // 400 BAD REQUEST — verificación de reCAPTCHA fallida
    @ExceptionHandler(InvalidRecaptchaException.class)
    public ResponseEntity<MessageResponseDTO> handleInvalidRecaptcha(InvalidRecaptchaException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Verificación de seguridad falló. Por favor intenta de nuevo.");
    }

    // ========================================
    // IMAGE EXCEPTIONS
    // ========================================

    // 500 INTERNAL SERVER ERROR — fallo al subir imagen
    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<MessageResponseDTO> handleImageUpload(ImageUploadException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo subir la imagen. Intenta de nuevo.");
    }

    // ========================================
    // PQRS EXCEPTIONS
    // ========================================

    // 400 BAD REQUEST — PQRS ya eliminada
    @ExceptionHandler(PqrsAlreadyDeletedException.class)
    public ResponseEntity<MessageResponseDTO> handlePqrsAlreadyDeleted(PqrsAlreadyDeletedException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "PQRS ya eliminada");
    }

    // ========================================
    // DATABASE EXCEPTIONS
    // ========================================

    // 409 CONFLICT — violación de integridad (únicos/FK)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<MessageResponseDTO> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Conflicto de integridad de datos";
        String cause = ex.getMostSpecificCause() != null && ex.getMostSpecificCause().getMessage() != null
                ? ex.getMostSpecificCause().getMessage().toLowerCase()
                : (ex.getMessage() == null ? "" : ex.getMessage().toLowerCase());
        if (cause.contains("unique") || cause.contains("duplicate") || cause.contains("uq_")) {
            message = "Registro duplicado: ya existe un recurso con esos datos";
        } else if (cause.contains("foreign key") || cause.contains("fk_")) {
            message = "Referencia inválida a datos relacionados";
        }
        return buildErrorResponse(HttpStatus.CONFLICT, message);
    }

    // ========================================
    // VALIDATION EXCEPTIONS
    // ========================================

    // 400 BAD REQUEST — argumentos de negocio inválidos
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageResponseDTO> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST,ex.getMessage());
    }

    // 400 BAD REQUEST — request mal formado o parámetro faltante
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<MessageResponseDTO> handleMissingParam(MissingServletRequestParameterException ex) {
        String mensaje = String.format("El parámetro '%s' es requerido", ex.getParameterName());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, mensaje);
    }


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<MessageResponseDTO> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Tipo de contenido no soportado. Usa multipart/form-data o application/json");
    }

    // ========================================
    // SECURITY EXCEPTIONS
    // ========================================

    // 403 FORBIDDEN — denegado por seguridad
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<MessageResponseDTO> handleAccessDenied(AccessDeniedException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "No tienes permisos para realizar esta acción");
    }

    // ========================================
    // HTTP EXCEPTIONS
    // ========================================

    // 405 METHOD NOT ALLOWED
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<MessageResponseDTO> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED,
                String.format("Método '%s' no permitido para esta ruta", ex.getMethod()));
    }

    // ========================================
    // PRODUCT EXCEPTIONS
    // ========================================

    // 404 NOT FOUND — categoría de producto no encontrada
    @ExceptionHandler(ProductCategoryNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleProductCategoryNotFound(
            ProductCategoryNotFoundException ex,
            WebRequest request) {
        return buildErrorResponseDTO(HttpStatus.NOT_FOUND, "Categoría no encontrada", request);
    }

    // 404 NOT FOUND — entidad JPA no encontrada
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleJpaEntityNotFound(
            EntityNotFoundException ex,
            WebRequest request) {

        String userMessage = "Recurso no encontrado";

        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("ProductCategoryEntity")) {
                userMessage = "Error: El producto tiene una categoría inexistente en la base de datos";
            } else if (ex.getMessage().contains("ProductEntity")) {
                userMessage = "Producto no encontrado";
            }
        }

        return buildErrorResponseDTO(HttpStatus.NOT_FOUND, userMessage, request);
    }
    @ExceptionHandler(PhoneRequiredException.class)
    public ResponseEntity<MessageResponseDTO> handlePhoneRequired(PhoneRequiredException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new MessageResponseDTO(ex.getMessage(), false));
    }



    // ========================================
    // FALLBACK
    // ========================================

    // 500 INTERNAL SERVER ERROR — fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponseDTO> handleGenericException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }
}
