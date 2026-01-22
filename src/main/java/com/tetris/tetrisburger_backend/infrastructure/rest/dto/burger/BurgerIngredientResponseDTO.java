// src/main/java/com/tetris/tetrisburger_backend/infrastructure/rest/dto/BurgerIngredientResponse.java
package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger;

import java.math.BigDecimal;

public record BurgerIngredientResponseDTO(
        Integer idBurgerIngredient,
        Integer idProduct,
        BigDecimal priceAtTime,
        Integer quantity,
        Boolean isOptional
) {
}
