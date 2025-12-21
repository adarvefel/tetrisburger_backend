package com.tetris.tetrisburger_backend.domain.exception;

public class MenuNotFoundException extends RuntimeException {
    public MenuNotFoundException(Integer idMenu) {
        super("Menú no encontrado con ID: " + idMenu);
    }

    public MenuNotFoundException(String message) {
        super(message);
    }
}
