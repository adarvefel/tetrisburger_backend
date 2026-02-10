package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger;

import com.tetris.tetrisburger_backend.domain.common.ImageStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public record BurgerResponseDTO(
        Integer idBurger,
        String name,
        String description,
        BigDecimal finalPrice,
        Boolean isFavorite,
        String imageUrl,
        String imageStatus,
        Integer idUser,
        Integer timesOrdered,
        List<BurgerIngredientResponseDTO> ingredients,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
