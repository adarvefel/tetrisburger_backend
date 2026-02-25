package com.tetris.tetrisburger_backend.infrastructure.rest.advice;

import com.tetris.tetrisburger_backend.infrastructure.rest.dto.ErrorResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.ValidationErrorResponseDTO;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Order(1)
public class ValidationExceptionHandler {

    // ========================================
    // MÉTODOS AUXILIARES
    // ========================================

    private String extractPath(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            return ((ServletWebRequest) request).getRequest().getRequestURI();
        }
        return request.getDescription(false).replace("uri=", "");
    }

    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(
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

    private ResponseEntity<ValidationErrorResponseDTO> buildValidationErrorResponse(
            HttpStatus status, String message, Map<String, String> errors, WebRequest request) {
        ValidationErrorResponseDTO errorResponse = new ValidationErrorResponseDTO(
                status.value(),
                status.getReasonPhrase(),
                message,
                LocalDateTime.now(),
                extractPath(request),
                errors
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    // ========================================
    // VALIDATION EXCEPTIONS
    // ========================================

    // 400 BAD REQUEST — tipo de parámetro incorrecto (@PathVariable/@RequestParam)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {

        String paramName = ex.getName();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "válido";
        String providedValue = ex.getValue() != null ? ex.getValue().toString() : "null";

        String message = String.format(
                "El parámetro '%s' debe ser de tipo %s. Valor recibido: '%s'",
                paramName, requiredType, providedValue
        );

        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    // 400 BAD REQUEST — parte requerida faltante en multipart
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingServletRequestPart(
            MissingServletRequestPartException ex, WebRequest request) {
        String message = String.format("El campo '%s' es requerido", ex.getRequestPartName());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    // 400 BAD REQUEST — @Valid en body (Bean Validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        err -> err.getField(),
                        err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : "Error de validación",
                        (existing, replacement) -> existing
                ));

        return buildValidationErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Errores de validación en los datos enviados",
                errors,
                request
        );
    }

    // 400 BAD REQUEST — validación en @RequestParam/@PathVariable
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {

        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        v -> v.getMessage(),
                        (a, b) -> a
                ));

        return buildValidationErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Errores de validación en los parámetros",
                errors,
                request
        );
    }

    // 400 BAD REQUEST — binding de query/form
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handleBindException(
            BindException ex, WebRequest request) {

        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        err -> err.getField(),
                        err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : "Error de validación",
                        (a, b) -> a
                ));

        return buildValidationErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Los datos enviados no son válidos. Por favor revísalos.",
                errors,
                request
        );
    }

    // 413 PAYLOAD TOO LARGE — imagen excede tamaño máximo
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDTO> handleMaxUploadSize(
            MaxUploadSizeExceededException ex, WebRequest request) {
        return buildErrorResponse(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "El archivo excede el tamaño máximo permitido (5MB)",
                request
        );
    }

}
