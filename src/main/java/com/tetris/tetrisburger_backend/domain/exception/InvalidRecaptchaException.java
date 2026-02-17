package com.tetris.tetrisburger_backend.domain.exception;

public class InvalidRecaptchaException extends RuntimeException {
    public InvalidRecaptchaException(String message) {
        super(message);
    }
}
