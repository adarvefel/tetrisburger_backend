package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger;

import java.math.BigDecimal;

public record MenuBurgerIngredientResponseDTO(
        Integer idBurgerIngredient,
        Integer idProduct,
        BigDecimal priceAtTime,
        Integer quantity,
        boolean isOptional
) {
}
