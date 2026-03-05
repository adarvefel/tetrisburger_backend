package com.tetris.tetrisburger_backend.domain.port.in.burger.command;

import java.math.BigDecimal;
import java.util.List;

public record UpdateMenuBurgerCommand(
        Integer idBurger,
        String name,
        String description,
        BigDecimal finalPrice,
        Boolean availability,
        Boolean isFeatured,
        List<IngredientRequest> ingredients,
        Integer updatedBy
) {
    public record IngredientRequest(
            Integer idProduct,
            Integer quantity,
            boolean isOptional
    ) {}
}