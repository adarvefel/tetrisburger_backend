package com.tetris.tetrisburger_backend.domain.exception;

public class PhoneRequiredException extends RuntimeException {
    public PhoneRequiredException(String message) {
        super(message);
    }
}
