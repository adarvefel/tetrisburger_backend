
package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger;


import java.util.List;

public record CreateCustomBurgerRequestDTO(
        String name,
        String description,
        List<IngredientRequestDTO> ingredients
) {
}
