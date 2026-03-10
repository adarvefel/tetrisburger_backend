package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.client;


import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.BurgerIngredientResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public record BurgerResponseDTO(
        Integer idBurger,
        String name,
        BigDecimal finalPrice,
        String imageUrl,
        List<BurgerIngredientResponseDTO> ingredients
){
}
