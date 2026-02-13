package com.tetris.tetrisburger_backend.domain.exception;


public class BurgerCreationException extends DomainException {

    public BurgerCreationException(String message) {
        super(message);
    }

    public BurgerCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}