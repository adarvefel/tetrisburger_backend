// src/main/java/com/tetris/tetrisburger_backend/infrastructure/rest/dto/burger/UpdateMenuBurgerRequestDTO.java
package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UpdateMenuBurgerRequestDTO(
        String name,
        String description,
        String imageUrl,
        List<IngredientRequestDTO> ingredients,
        Boolean availability,
        Boolean favorite,
        Boolean onMenu
) {
}
