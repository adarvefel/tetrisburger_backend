package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger;

import java.util.List;

public record UpdateCustomBurgerRequestDTO(
        String name,
        String description,
        String imageUrl,
        List<IngredientRequestDTO> ingredients
) {
}
