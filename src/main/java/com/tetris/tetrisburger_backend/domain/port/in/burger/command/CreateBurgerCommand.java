package com.tetris.tetrisburger_backend.domain.port.in.burger.command;

import java.math.BigDecimal;
import java.util.List;

public record CreateBurgerCommand(
        String name,
        String description,
        String imageUrl,
        List<IngredientRequest> ingredients,
        boolean isFavorite,
        BigDecimal finalPrice
) {
}
