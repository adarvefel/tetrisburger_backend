package com.tetris.tetrisburger_backend.domain.exception;

public class AdditionAlreadyDeletedException extends RuntimeException {
    public AdditionAlreadyDeletedException(String message) {
        super(message);
    }
}
