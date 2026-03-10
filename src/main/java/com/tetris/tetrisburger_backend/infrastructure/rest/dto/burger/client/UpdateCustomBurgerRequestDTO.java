package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.client;

import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.IngredientRequestDTO;

import java.util.List;

public record UpdateCustomBurgerRequestDTO(
        String name,
        String description,
        List<IngredientRequestDTO> ingredients
) {
}
