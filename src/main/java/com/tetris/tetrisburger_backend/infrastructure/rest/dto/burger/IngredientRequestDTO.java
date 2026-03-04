package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger;

public record IngredientRequestDTO(
        Integer idProduct,
        Integer quantity,
        Boolean isOptional
) {
}
