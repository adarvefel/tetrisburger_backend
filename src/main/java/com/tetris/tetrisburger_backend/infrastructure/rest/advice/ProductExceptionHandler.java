package com.tetris.tetrisburger_backend.infrastructure.rest.advice;

import com.tetris.tetrisburger_backend.domain.exception.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.ErrorResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.MessageResponseDTO;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
@Order(10)
public class ProductExceptionHandler {

    
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
    // PRODUCT EXCEPTIONS
    // ========================================

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<MessageResponseDTO> handleProductAlreadyExists(
            ProductAlreadyExistsException ex, WebRequest request) {
                return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<MessageResponseDTO> handleProductNotFound(
            ProductNotFoundException ex, WebRequest request) {
                return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ProductAlreadyDeletedException.class)
    public ResponseEntity<MessageResponseDTO> handleProductAlreadyDeleted(
            ProductAlreadyDeletedException ex, WebRequest request) {
                return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ProductNotAvailableException.class)
    public ResponseEntity<ErrorResponseDTO> handleProductNotAvailable(
            ProductNotAvailableException ex, WebRequest request) {
                return buildErrorResponseDTO(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(ProductCategoryNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleProductCategoryNotFound(
            ProductCategoryNotFoundException ex, WebRequest request) {
                return buildErrorResponseDTO(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }
}
