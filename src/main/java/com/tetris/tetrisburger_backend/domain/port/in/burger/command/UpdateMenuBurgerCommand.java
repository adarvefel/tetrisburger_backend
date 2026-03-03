package com.tetris.tetrisburger_backend.domain.port.in.burger.command;

import java.util.List;

public record UpdateMenuBurgerCommand(
        Integer idBurger,
        String name,
        String description,
        Boolean availability,
        List<IngredientRequest> ingredients,
        Integer updatedBy
) {
    public record IngredientRequest(
            Integer idProduct,
            Integer quantity
    ) {}
}
