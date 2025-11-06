package com.tetris.tetrisburger_backend.domain.exception;

public class ProductNotFoundException extends DomainException {
    public ProductNotFoundException(Integer id) {
        super("Producto no encontrado con el id=" + id);
    }
}