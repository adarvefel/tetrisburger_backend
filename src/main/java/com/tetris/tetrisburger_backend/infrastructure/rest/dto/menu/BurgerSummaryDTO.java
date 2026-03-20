package com.tetris.tetrisburger_backend.infrastructure.rest.dto.menu;

import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.BurgerIngredientResponseDTO;

import java.util.List;

public record BurgerSummaryDTO(
        Integer idBurger,
        String name,
        String description,
        Double finalPrice,
        String imageUrl,
        List<BurgerIngredientResponseDTO> ingredients
) {}