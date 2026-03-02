package com.tetris.tetrisburger_backend.domain.exception;

public class AdditionNotFoundException extends RuntimeException {
    public AdditionNotFoundException(String message) {
        super(message);
    }
}
