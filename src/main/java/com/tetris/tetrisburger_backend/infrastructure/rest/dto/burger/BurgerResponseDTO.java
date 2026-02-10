package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger;


import com.tetris.tetrisburger_backend.domain.common.ImageStatus;

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
        ImageStatus imageStatus,
        Integer timesOrdered,
        List<BurgerIngredientResponseDTO> ingredients
){
}
