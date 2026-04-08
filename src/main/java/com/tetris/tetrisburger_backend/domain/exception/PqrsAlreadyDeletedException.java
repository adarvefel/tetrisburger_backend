package com.tetris.tetrisburger_backend.domain.exception;

public class PqrsAlreadyDeletedException extends RuntimeException {
    public PqrsAlreadyDeletedException(String message) {
        super(message);
    }
}
