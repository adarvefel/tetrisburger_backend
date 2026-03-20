package com.tetris.tetrisburger_backend.domain.exception;

/**
 * Excepción lanzada cuando un usuario no tiene permisos para realizar una acción
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
