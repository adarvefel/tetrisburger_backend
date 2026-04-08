package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.admin;

import com.tetris.tetrisburger_backend.domain.model.BurgerSettings;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BurgerSettingsResponseDTO(
        BigDecimal customBurgerMinPrice,
        BigDecimal customBurgerMaxPrice,
        Integer minIngredients,
        Integer maxIngredients,
        Boolean customBurgersEnabled,
        String minPriceFormatted,
        String maxPriceFormatted,
        LocalDateTime updatedAt,
        Integer updatedBy
) {
    public static BurgerSettingsResponseDTO from(BurgerSettings settings) {
        return new BurgerSettingsResponseDTO(
                settings.getCustomBurgerMinPrice(),
                settings.getCustomBurgerMaxPrice(),
                settings.getMinIngredients(),
                settings.getMaxIngredients(),
                settings.isCustomBurgersEnabled(),
                String.format("$%,.0f COP", settings.getCustomBurgerMinPrice()),
                String.format("$%,.0f COP", settings.getCustomBurgerMaxPrice()),
                settings.getUpdatedAt(),
                settings.getUpdatedBy()
        );
    }
}
