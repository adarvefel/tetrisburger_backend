package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record MenuBurgerResponseDTO(
        Integer idBurger,
        String name,
        String description,
        BigDecimal basePrice,
        BigDecimal finalPrice,
        boolean isOnMenu,
        boolean isFavorite,
        boolean isCustom,
        boolean availability,
        String imageUrl,
        Integer timesOrdered,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        Integer updatedBy,
        Integer createdBy,
        Integer deletedBy,
        List<MenuBurgerIngredientResponseDTO> ingredients
) {
}
