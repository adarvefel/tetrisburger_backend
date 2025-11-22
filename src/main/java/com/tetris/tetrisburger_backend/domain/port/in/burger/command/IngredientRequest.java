package com.tetris.tetrisburger_backend.domain.port.in.burger.command;

import java.math.BigDecimal;

public record IngredientRequest(
        Integer idProduct,
        Integer quantity,
        Boolean isOptional
) {

    /**
     * Compact constructor para validaciones
     */
    public IngredientRequest {
        if (idProduct == null) {
            throw new IllegalArgumentException("El ID del producto es obligatorio");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        if (isOptional == null) {
            isOptional = false;  // Valor por defecto
        }
    }
}