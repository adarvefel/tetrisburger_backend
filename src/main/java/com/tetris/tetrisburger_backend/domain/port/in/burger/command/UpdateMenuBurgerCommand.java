package com.tetris.tetrisburger_backend.domain.port.in.burger.command;

import java.util.List;

public record UpdateMenuBurgerCommand(
        Integer idBurger,
        String name,
        String description,
        List<IngredientRequest> ingredients,
        Boolean availability,
        Boolean isFavorite,
        Integer updatedBy

) {
    public record IngredientRequest(
            Integer idProduct,
            Integer quantity,
            Boolean isOptional
    ) {}
}
