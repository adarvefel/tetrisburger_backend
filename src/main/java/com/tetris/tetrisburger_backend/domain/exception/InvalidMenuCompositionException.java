package com.tetris.tetrisburger_backend.domain.exception;

public class InvalidMenuCompositionException extends RuntimeException {
    public InvalidMenuCompositionException(String message) {
        super(message);
    }
}
