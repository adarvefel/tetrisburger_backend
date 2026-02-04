package com.tetris.tetrisburger_backend.infrastructure.rest.advice;

import com.tetris.tetrisburger_backend.domain.exception.ProductAlreadyDeletedException;
import com.tetris.tetrisburger_backend.domain.exception.ProductAlreadyExistsException;
import com.tetris.tetrisburger_backend.domain.exception.ProductNotAvailableException;
import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.ErrorResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.MessageResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
@Order(10)
public class ProductExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProductExceptionHandler.class);

    private ResponseEntity<MessageResponseDTO> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new MessageResponseDTO(message, false));
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<MessageResponseDTO> handleProductAlreadyExists(
            ProductAlreadyExistsException ex, WebRequest request) {
        logger.warn("Producto duplicado: {} - Path: {}", ex.getMessage(), request.getDescription(false));
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<MessageResponseDTO> handleProductNotFound(
            ProductNotFoundException ex, WebRequest request) {
        logger.warn("Producto no encontrado: {} - Path: {}", ex.getMessage(), request.getDescription(false));
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ProductAlreadyDeletedException.class)
    public ResponseEntity<MessageResponseDTO> handleProductAlreadyDeleted(
            ProductAlreadyDeletedException ex, WebRequest request) {
        logger.warn("Producto ya eliminado: {} - Path: {}", ex.getMessage(), request.getDescription(false));
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ProductNotAvailableException.class)
    public ResponseEntity<ErrorResponseDTO> handleProductNotAvailable(
            ProductNotAvailableException ex
    ) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpStatus.CONFLICT.value(),              // status: 409
                HttpStatus.CONFLICT.getReasonPhrase(),    // error: "Conflict"
                ex.getMessage(),                          // message: "Producto no disponible: Pan Brioche"
                LocalDateTime.now()                       // timestamp
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

}
