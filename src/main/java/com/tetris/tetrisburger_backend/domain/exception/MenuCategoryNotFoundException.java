package com.tetris.tetrisburger_backend.domain.exception;

public class MenuCategoryNotFoundException extends RuntimeException {
    public MenuCategoryNotFoundException(Integer idMenuCategory) {
        super("Categoría de menú no encontrada con ID: " + idMenuCategory);
    }

    public MenuCategoryNotFoundException(String message) {
        super(message);
    }
}
