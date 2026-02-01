package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger;

import java.math.BigDecimal;

public record MenuBurgerIngredientResponseDTO(
        Integer idBurgerIngredient,
        Integer idProduct,
        String productName,
        BigDecimal priceAtTime,
        Integer quantity,
        BigDecimal subtotal,
        Boolean isOptional
){
}
