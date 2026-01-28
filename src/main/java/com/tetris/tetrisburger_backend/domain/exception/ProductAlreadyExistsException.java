package com.tetris.tetrisburger_backend.domain.exception;

public class ProductAlreadyExistsException extends DomainException {
    public ProductAlreadyExistsException(String name) {
        super("Ya existe un producto con el nombre: " + name);
    }
}