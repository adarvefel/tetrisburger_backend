package com.tetris.tetrisburger_backend.domain.exception;

public class ProductCategoryNotFoundException extends RuntimeException {

    public ProductCategoryNotFoundException(String message) {
        super(message);
    }

    public ProductCategoryNotFoundException(Integer categoryId) {
        super("Categoría de producto no encontrada con ID: " + categoryId);
    }

    public ProductCategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
