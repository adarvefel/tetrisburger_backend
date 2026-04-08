package com.tetris.tetrisburger_backend.domain.exception;

/**
 * Excepción lanzada cuando un producto no cumple con las reglas de validación
 */
public class InvalidProductException extends RuntimeException {

    public InvalidProductException(String message) {
        super(message);
    }

    public InvalidProductException(String message, Throwable cause) {
        super(message, cause);
    }
}
