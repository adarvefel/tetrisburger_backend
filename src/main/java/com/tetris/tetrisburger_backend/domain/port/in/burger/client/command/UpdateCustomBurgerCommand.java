package com.tetris.tetrisburger_backend.domain.port.in.burger.client.command;

import java.util.List;

public record UpdateCustomBurgerCommand(
        Integer idBurger,
        Integer idUser,
        String name,
        String description,
        List<IngredientRequest> ingredients
) {
    public record IngredientRequest(
            Integer idProduct,
            Integer quantity
    ) {
    }
}
