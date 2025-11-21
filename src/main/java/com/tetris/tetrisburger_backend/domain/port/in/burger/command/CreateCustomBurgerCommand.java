package com.tetris.tetrisburger_backend.domain.port.in.burger.command;

import java.util.List;

public record CreateCustomBurgerCommand(
        String name,
        String description,
        String imageUrl,
        Integer idUser,
        List<IngredientRequest> ingredients
) {
}
