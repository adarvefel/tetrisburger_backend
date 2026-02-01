package com.tetris.tetrisburger_backend.infrastructure.rest.advice;

import com.tetris.tetrisburger_backend.domain.exception.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.MessageResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Order(100)
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<MessageResponseDTO> buildErrorResponse(String message) {
        return ResponseEntity.badRequest().body(new MessageResponseDTO(message, false));
    }

    private ResponseEntity<MessageResponseDTO> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new MessageResponseDTO(message, false));
    }

    // ========================================
    // USER EXCEPTIONS
    // ========================================

    // 409 CONFLICT — reglas de negocio/únicos
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<MessageResponseDTO> handleUserExists(UserAlreadyExistsException ex, WebRequest request) {
        logger.warn("Usuario ya existe: {} - Path: {}", ex.getMessage(), request.getDescription(false));
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }




    // 404 NOT FOUND — recursos no encontrados
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<MessageResponseDTO> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        logger.warn("Usuario no encontrado: {} - Path: {}", ex.getMessage(), request.getDescription(false));
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 401 UNAUTHORIZED — credenciales inválidas
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<MessageResponseDTO> handleInvalidCredentials(InvalidCredentialsException ex, WebRequest request) {
        logger.warn("Credenciales inválidas - Path: {}", request.getDescription(false));
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
    }

    // 400 BAD REQUEST — token inválido
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<MessageResponseDTO> handleInvalidToken(InvalidTokenException ex, WebRequest request) {
        logger.warn("Token inválido: {} - Path: {}", ex.getMessage(), request.getDescription(false));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ========================================
    // IMAGE EXCEPTIONS
    // ========================================

    // 500 INTERNAL SERVER ERROR — fallo al subir imagen
    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<MessageResponseDTO> handleImageUpload(ImageUploadException ex, WebRequest request) {
        logger.error("Error al subir imagen: {} - Path: {}", ex.getMessage(), request.getDescription(false), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo subir la imagen. Intenta de nuevo.");
    }

    // ========================================
    // PQRS EXCEPTIONS
    // ========================================

    // 400 BAD REQUEST — PQRS ya eliminada
    @ExceptionHandler(PqrsAlreadyDeletedException.class)
    public ResponseEntity<MessageResponseDTO> handlePqrsAlreadyDeleted(PqrsAlreadyDeletedException ex, WebRequest request) {
        logger.warn("PQRS ya eliminada o no encontrada: {} - Path: {}", ex.getMessage(), request.getDescription(false));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ========================================
    // DATABASE EXCEPTIONS
    // ========================================

    // 409 CONFLICT — violación de integridad (únicos/FK)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<MessageResponseDTO> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        logger.error("Error de integridad de datos - Path: {}", request.getDescription(false), ex);
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
    public ResponseEntity<MessageResponseDTO> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        logger.warn("Argumento inválido: {} - Path: {}", ex.getMessage(), request.getDescription(false));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 400 BAD REQUEST — request mal formado o parámetro faltante
    @ExceptionHandler({MissingServletRequestParameterException.class, HttpMediaTypeNotSupportedException.class})
    public ResponseEntity<MessageResponseDTO> handleBadRequest(Exception ex, HttpServletRequest req) {
        logger.warn("Bad request: {} - Path: {}", ex.getMessage(), req.getRequestURI());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ========================================
    // SECURITY EXCEPTIONS
    // ========================================

    // 403 FORBIDDEN — denegado por seguridad
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<MessageResponseDTO> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        logger.warn("Access denied - Path: {}", req.getRequestURI());
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Acceso denegado");
    }

    // ========================================
    // HTTP EXCEPTIONS
    // ========================================

    // 405 METHOD NOT ALLOWED
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<MessageResponseDTO> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        logger.warn("Method not allowed: {} - Path: {}", ex.getMessage(), req.getRequestURI());
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
    }

    // ========================================
    // FALLBACK
    // ========================================

    // 500 INTERNAL SERVER ERROR — fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponseDTO> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Error inesperado en {} - Tipo: {} - Mensaje: {}",
                request.getDescription(false), ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }
}
