package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger;

import com.tetris.tetrisburger_backend.domain.common.FileData;

import java.math.BigDecimal;
import java.util.List;

public record CreateMenuBurgerRequestDTO(
        String name,
        String description,
        List<IngredientRequestDTO> ingredients,
        BigDecimal finalPrice,
        Boolean isFavorite
) {
}
