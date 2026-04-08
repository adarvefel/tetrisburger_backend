package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.admin;

import java.math.BigDecimal;

public record MenuBurgerIngredientResponseDTO(
        Integer idBurgerIngredient,
        Integer idProduct,
        String productName,
        BigDecimal priceAtTime,
        Integer quantity,
        BigDecimal subtotal,
        boolean isOptional,
        String imageUrl
){
}
