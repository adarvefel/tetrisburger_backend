package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger;

import java.math.BigDecimal;
import java.util.List;

public record CreateMenuBurgerRequestDTO(
        String name,
        String description,
        String imageUrl,
        boolean favorite,
        List<IngredientRequestDTO> ingredients,
        BigDecimal finalPrice
) {
}
