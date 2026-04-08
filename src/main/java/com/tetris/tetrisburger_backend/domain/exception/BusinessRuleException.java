package com.tetris.tetrisburger_backend.domain.exception;

/**
 * Excepción lanzada cuando se viola una regla de negocio
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }

    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
