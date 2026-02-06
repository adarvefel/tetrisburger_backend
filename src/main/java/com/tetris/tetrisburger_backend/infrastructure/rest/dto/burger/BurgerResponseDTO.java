package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger;

import com.tetris.tetrisburger_backend.domain.common.ImageStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record BurgerResponseDTO(
        Integer idBurger,
        String name,
        String description,
        BigDecimal basePrice,
        BigDecimal finalPrice,
        Boolean isOnMenu,
        Boolean isFavorite,
        Boolean isCustom,
        Boolean availability,
        String imageUrl,
        String imageKey,
        String imageStatus,
        Integer idUser,
        Integer timesOrdered,
        List<BurgerIngredientResponseDTO> ingredients,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt,
        Integer createdBy,
        Integer updatedBy,
        Integer deletedBy
) {
}
