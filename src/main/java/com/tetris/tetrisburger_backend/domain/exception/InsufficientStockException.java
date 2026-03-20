package com.tetris.tetrisburger_backend.domain.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String productName, Integer requested, Integer available) {
        super(String.format(
                "Stock insuficiente para '%s'. Solicitado: %d, Disponible: %d",
                productName, requested, available
        ));
    }

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }
}