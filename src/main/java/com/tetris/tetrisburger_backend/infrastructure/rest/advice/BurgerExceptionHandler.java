package com.tetris.tetrisburger_backend.infrastructure.rest.advice;

import com.tetris.tetrisburger_backend.domain.exception.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.ErrorResponseDTO;
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
@Order(1)
public class BurgerExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(BurgerExceptionHandler.class);

    /**
     * Construye una respuesta de error estándar
     */
    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(
            HttpStatus status,
            String message,
            WebRequest request) {

        String path = request.getDescription(false).replace("uri=", "");

        ErrorResponseDTO error = new ErrorResponseDTO(
                status.value(),
                status.getReasonPhrase(),
                message,
                LocalDateTime.now(),
                path
        );

        return ResponseEntity.status(status).body(error);
    }

    // ========================================
    // EXCEPCIONES DE BURGER
    // ========================================

    @ExceptionHandler(BurgerCreationException.class)
    public ResponseEntity<ErrorResponseDTO> handleBurgerCreation(
            BurgerCreationException ex,
            WebRequest request) {

        String message = ex.getMessage();

        if (message != null && (
                message.contains("Ya existe") ||
                        message.contains("duplicado") ||
                        message.toLowerCase().contains("con el nombre")
        )) {
            logger.warn("Error de validación al crear hamburguesa: {} - Ruta: {}",
                    message, request.getDescription(false));

            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    message,
                    request
            );
        }

        logger.error("Error técnico al crear/actualizar hamburguesa: {} - Ruta: {}",
                message, request.getDescription(false), ex);

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error al procesar la hamburguesa. Intenta de nuevo.",
                request
        );
    }

    @ExceptionHandler(InvalidBurgerException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidBurger(
            InvalidBurgerException ex,
            WebRequest request) {

        logger.warn("Hamburguesa inválida: {} - Ruta: {}",
                ex.getMessage(), request.getDescription(false));

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(BurgerNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleBurgerNotFound(
            BurgerNotFoundException ex,
            WebRequest request) {

        logger.warn("Hamburguesa no encontrada: {} - Ruta: {}",
                ex.getMessage(), request.getDescription(false));

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request
        );
    }


    @ExceptionHandler(InvalidSettingsException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidSettings(
            InvalidSettingsException ex,
            WebRequest request) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleProductNotFound(
            ProductNotFoundException ex,
            WebRequest request) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request
        );
    }

    // ========================================
    // EXCEPCIÓN GLOBAL (RECOMENDADA)
    // ========================================


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(
            IllegalArgumentException ex,
            WebRequest request) {



        return buildErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request
        );
    }
}