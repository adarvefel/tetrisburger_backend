package com.tetris.tetrisburger_backend.infrastructure.rest.advice;

import com.tetris.tetrisburger_backend.infrastructure.rest.dto.MessageResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;  // ✅ AGREGAR IMPORT
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Order(1)
public class ValidationExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ValidationExceptionHandler.class);

    private ResponseEntity<MessageResponseDTO> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new MessageResponseDTO(message, false));
    }

    private ResponseEntity<Map<String, Object>> buildValidationErrorResponse(
            HttpStatus status, String message, Map<String, String> errors) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("success", false);
        response.put("timestamp", System.currentTimeMillis());
        response.put("errors", errors);
        return ResponseEntity.status(status).body(response);
    }

    // ========================================
    // VALIDATION EXCEPTIONS
    // ========================================

    // ✅ AGREGAR ESTE HANDLER
    // 400 BAD REQUEST — tipo de parámetro incorrecto (@PathVariable/@RequestParam)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<MessageResponseDTO> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest req) {

        String paramName = ex.getName();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "válido";
        String providedValue = ex.getValue() != null ? ex.getValue().toString() : "null";

        String message = String.format(
                "El parámetro '%s' debe ser de tipo %s. Valor recibido: '%s'",
                paramName,
                requiredType,
                providedValue
        );

        logger.warn("⚠️ Type mismatch - Parámetro: {} - Esperado: {} - Recibido: {} - Path: {}",
                paramName, requiredType, providedValue, req.getRequestURI());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    // 400 BAD REQUEST — parte requerida faltante en multipart
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<MessageResponseDTO> handleMissingServletRequestPart(
            MissingServletRequestPartException ex, WebRequest request) {
        String partName = ex.getRequestPartName();
        String message = String.format("El campo '%s' es requerido", partName);
        logger.warn("⚠️ Parte de request faltante: {} - Path: {}", partName, request.getDescription(false));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    // 400 BAD REQUEST — @Valid en body (Bean Validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        logger.warn("⚠️ Errores de validación - Path: {}", request.getDescription(false));
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        err -> err.getField(),
                        err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : "Error de validación",
                        (existing, replacement) -> existing
                ));
        return buildValidationErrorResponse(HttpStatus.BAD_REQUEST, "Errores de validación", errors);
    }

    // 400 BAD REQUEST — validación en @RequestParam/@PathVariable
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        logger.warn("⚠️ Constraint violation - Path: {}", req.getRequestURI());
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        v -> v.getMessage(),
                        (a, b) -> a
                ));
        return buildValidationErrorResponse(HttpStatus.BAD_REQUEST, "Errores de validación", errors);
    }

    // 400 BAD REQUEST — binding de query/form
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> handleBindException(BindException ex, HttpServletRequest req) {
        logger.warn("⚠️ BindException - Path: {}", req.getRequestURI());
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        err -> err.getField(),
                        err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : "Error de validación",
                        (a, b) -> a
                ));
        return buildValidationErrorResponse(HttpStatus.BAD_REQUEST, "Errores de validación", errors);
    }

    // 413 PAYLOAD TOO LARGE — imagen excede tamaño máximo
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<MessageResponseDTO> handleMaxUploadSize(MaxUploadSizeExceededException ex, WebRequest request) {
        logger.warn("⚠️ Archivo excede tamaño máximo - Path: {}", request.getDescription(false));
        return buildErrorResponse(HttpStatus.PAYLOAD_TOO_LARGE, "El archivo excede el tamaño máximo permitido (5MB)");
    }
}
