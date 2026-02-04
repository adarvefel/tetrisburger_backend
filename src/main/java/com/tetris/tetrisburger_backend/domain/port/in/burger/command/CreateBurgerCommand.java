package com.tetris.tetrisburger_backend.domain.port.in.burger.command;

import com.tetris.tetrisburger_backend.domain.common.FileData;

import java.math.BigDecimal;
import java.util.List;

public record CreateBurgerCommand(
        String name,
        String description,
        FileData imageData,
        List<IngredientRequest> ingredients,
        Boolean isFavorite,
        BigDecimal finalPrice,
        Integer createdBy
) {
    public CreateBurgerCommand {
        // Validaciones básicas (las detalladas se hacen en el UseCase)
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("Debe incluir al menos un ingrediente");
        }

        if (createdBy == null) {
            throw new IllegalArgumentException("El ID del creador es obligatorio");
        }

        if (finalPrice != null && finalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio final debe ser mayor a cero");
        }

        if (isFavorite == null) {
            isFavorite = false;
        }
    }

    public record IngredientRequest(
            Integer idProduct,
            Integer quantity,
            Boolean isOptional
    ) {
        public IngredientRequest {
            if (idProduct == null) {
                throw new IllegalArgumentException("El ID del producto no puede ser nulo");
            }

            if (quantity == null || quantity <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
            }

            if (isOptional == null) {
                isOptional = false;
            }
        }
    }
}
