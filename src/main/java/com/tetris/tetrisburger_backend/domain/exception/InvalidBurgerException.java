package com.tetris.tetrisburger_backend.domain.exception;

public class InvalidBurgerException extends DomainException {

    public InvalidBurgerException(String message) {
        super(message);
    }

    public InvalidBurgerException(String message, Throwable cause) {
        super(message, cause);
    }
}
