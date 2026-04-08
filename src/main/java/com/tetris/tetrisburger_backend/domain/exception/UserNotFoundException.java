package com.tetris.tetrisburger_backend.domain.exception;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotFoundException(Integer userId) {
        super("Usuario no encontrado con ID: " + userId);
    }
}
