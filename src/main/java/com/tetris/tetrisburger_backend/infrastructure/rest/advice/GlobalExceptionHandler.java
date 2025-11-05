package com.tetris.tetrisburger_backend.infrastructure.rest.advice;

import com.tetris.tetrisburger_backend.domain.exception.InvalidCredentialsException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidTokenException;
import com.tetris.tetrisburger_backend.domain.exception.UserAlreadyExistsException;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.MessageResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<MessageResponseDTO> buildErrorResponse(String message) {
        return ResponseEntity
                .badRequest()
                .body(new MessageResponseDTO(message, false));
    }

    private ResponseEntity<MessageResponseDTO> buildErrorResponse(
            HttpStatus status,
            String message) {
        return ResponseEntity
                .status(status)
                .body(new MessageResponseDTO(message, false));
    }

    private ResponseEntity<Map<String, Object>> buildValidationErrorResponse(
            HttpStatus status,
            String message,
            Map<String, String> errors) {

        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("success", false);
        response.put("timestamp", System.currentTimeMillis());
        response.put("errors", errors);

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<MessageResponseDTO> handleUserExists(
            UserAlreadyExistsException ex,
            WebRequest request) {
        logger.warn("Usuario ya existe: {} - Path: {}",
                ex.getMessage(), request.getDescription(false));
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<MessageResponseDTO> handleUserNotFound(
            UserNotFoundException ex,
            WebRequest request) {
        logger.warn("Usuario no encontrado: {} - Path: {}",
                ex.getMessage(), request.getDescription(false));
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<MessageResponseDTO> handleInvalidCredentials(
            InvalidCredentialsException ex,
            WebRequest request) {
        logger.warn("Credenciales inválidas - Path: {}",
                request.getDescription(false));
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<MessageResponseDTO> handleInvalidToken(
            InvalidTokenException ex,
            WebRequest request) {
        logger.warn("Token inválido: {} - Path: {}",
                ex.getMessage(), request.getDescription(false));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<MessageResponseDTO> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            WebRequest request) {

        logger.error("Error de integridad de datos - Path: {}",
                request.getDescription(false), ex);

        String message = "Conflicto de integridad de datos";

        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("email") || ex.getMessage().contains("unique")) {
                message = "El email ya está registrado en el sistema";
            } else if (ex.getMessage().contains("foreign key")) {
                message = "Referencia inválida a datos relacionados";
            }
        }

        return buildErrorResponse(HttpStatus.CONFLICT, message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageResponseDTO> handleIllegalArgument(
            IllegalArgumentException ex,
            WebRequest request) {
        logger.warn("Argumento inválido: {} - Path: {}",
                ex.getMessage(), request.getDescription(false));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        logger.warn("Errores de validación - Path: {}",
                request.getDescription(false));

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() != null
                                ? error.getDefaultMessage()
                                : "Error de validación",
                        (existing, replacement) -> existing
                ));

        return buildValidationErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Errores de validación",
                errors
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponseDTO> handleGenericException(
            Exception ex,
            WebRequest request) {

        logger.error("Error inesperado en {} - Tipo: {} - Mensaje: {}",
                request.getDescription(false),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                ex);

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor"
        );
    }
}