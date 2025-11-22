// src/main/java/com/tetris/tetrisburger_backend/domain/port/in/burger/command/UpdateCustomBurgerCommand.java
package com.tetris.tetrisburger_backend.domain.port.in.burger.command;

import java.util.List;

public record UpdateCustomBurgerCommand(
        Integer idBurger,
        Integer idUser,
        String name,
        String description,
        String imageUrl,
        List<IngredientRequest> ingredients
) {
}
