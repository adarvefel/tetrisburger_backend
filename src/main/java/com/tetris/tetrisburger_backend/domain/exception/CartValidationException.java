package com.tetris.tetrisburger_backend.domain.exception;

import java.util.List;

public class CartValidationException extends RuntimeException {
    private final List<String> errors;

    public CartValidationException(List<String> errors) {
        super("Hay ítems en el carrito que no pueden procesarse");
        this.errors = errors;
    }

    public List<String> getErrors() { return errors; }
}