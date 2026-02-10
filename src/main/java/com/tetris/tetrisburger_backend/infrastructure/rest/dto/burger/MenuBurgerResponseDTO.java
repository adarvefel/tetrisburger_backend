package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger;

import com.tetris.tetrisburger_backend.domain.common.ImageStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record MenuBurgerResponseDTO(
        Integer idBurger,
        String name,
        String description,
        BigDecimal basePrice,
        BigDecimal finalPrice,
        BigDecimal margin,
        BigDecimal marginPercentage,
        boolean sellingAtLoss,
        boolean isOnMenu,
        boolean isFavorite,
        boolean availability,
        String imageUrl,
        String imageKey,
        ImageStatus imageStatus,
        Integer timesOrdered,
        List<MenuBurgerIngredientResponseDTO> ingredients,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        Integer updatedBy,
        Integer createdBy,
        Integer deletedBy
) {
}
