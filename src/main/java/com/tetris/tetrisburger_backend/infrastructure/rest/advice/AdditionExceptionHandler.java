package com.tetris.tetrisburger_backend.infrastructure.rest.advice;

import com.tetris.tetrisburger_backend.domain.exception.AdditionAlreadyDeletedException;
import com.tetris.tetrisburger_backend.domain.exception.AdditionAlreadyExistsException;
import com.tetris.tetrisburger_backend.domain.exception.AdditionNotFoundException;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.MessageResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Order(10)
public class AdditionExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(AdditionExceptionHandler.class);

    private String extractPath(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            return ((ServletWebRequest) request).getRequest().getRequestURI();
        }
        return request.getDescription(false).replace("uri=", "");
    }

    private ResponseEntity<MessageResponseDTO> buildMessage(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new MessageResponseDTO(message, false));
    }

    @ExceptionHandler(AdditionNotFoundException.class)
    public ResponseEntity<MessageResponseDTO> handleNotFound(
            AdditionNotFoundException ex, WebRequest request) {
        logger.warn("Adición no encontrada: {} - Path: {}", ex.getMessage(), extractPath(request));
        return buildMessage(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(AdditionAlreadyExistsException.class)
    public ResponseEntity<MessageResponseDTO> handleAlreadyExists(
            AdditionAlreadyExistsException ex, WebRequest request) {
        logger.warn("Adición duplicada: {} - Path: {}", ex.getMessage(), extractPath(request));
        return buildMessage(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AdditionAlreadyDeletedException.class)
    public ResponseEntity<MessageResponseDTO> handleAlreadyDeleted(
            AdditionAlreadyDeletedException ex, WebRequest request) {
        logger.warn("Adición ya eliminada: {} - Path: {}", ex.getMessage(), extractPath(request));
        return buildMessage(HttpStatus.NOT_FOUND, ex.getMessage());
    }
}
