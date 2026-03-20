
package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.client;


import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.IngredientRequestDTO;

import java.util.List;

public record CreateCustomBurgerRequestDTO(
        String name,
        List<IngredientRequestDTO> ingredients
) {
}
