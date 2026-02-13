package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.user;


import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.BurgerIngredientResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public record BurgerResponseDTO(
        Integer idBurger,
        String name,
        String description,
        BigDecimal finalPrice,
        Boolean isFavorite,
        Boolean isAvailability,
        String imageUrl,
        String imageStatus,
        Integer timesOrdered,
        List<BurgerIngredientResponseDTO> ingredients
){
}
