package com.tetris.tetrisburger_backend.domain.port.in.burger.command;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.ImageStatus;

import java.math.BigDecimal;
import java.util.List;

public record CreateBurgerCommand(
        String name,
        String description,
        FileData  imageData,
        Boolean isFeatured,
        BigDecimal finalPrice,
        List<IngredientRequest> ingredients,
        Integer createdBy
) {
    public CreateBurgerCommand {
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
    }

    public record IngredientRequest(
            Integer idProduct,
            Integer quantity,
            Boolean isOptional
    ) {
        public IngredientRequest {
            if (idProduct == null || idProduct <= 0) {
                throw new IllegalArgumentException("ID producto debe ser positivo");
            }
            if (quantity == null || quantity <= 0) {
                throw new IllegalArgumentException("Cantidad debe ser > 0");
            }
        }
    }
}
