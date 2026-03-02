package com.tetris.tetrisburger_backend.domain.exception;

public class AdditionAlreadyExistsException extends RuntimeException {
    public AdditionAlreadyExistsException(String message) {
        super(message);
    }
}
