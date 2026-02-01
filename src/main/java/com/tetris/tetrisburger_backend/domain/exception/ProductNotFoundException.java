package com.tetris.tetrisburger_backend.domain.exception;

public class ProductNotFoundException extends DomainException {


        public ProductNotFoundException(String message) {
            super(message);
        }

        public ProductNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }

        public ProductNotFoundException(Integer productId) {
            super("Producto no encontrado con ID: " + productId);
        }


}