package com.tetris.tetrisburger_backend.domain.port.in.burger.command;

import java.util.List;

public record UpdateMenuBurgerCommand(
        Integer idBurger,
        String name,
        String description,
        String imageUrl,
        List<IngredientRequest> ingredients,
        Boolean availability,
        Boolean favorite,
        Boolean onMenu

) {
}
