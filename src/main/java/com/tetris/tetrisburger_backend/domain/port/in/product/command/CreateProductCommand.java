package com.tetris.tetrisburger_backend.domain.port.in.product.command;


import java.math.BigDecimal;

public record CreateProductCommand(
        String name,
        String description,
        Integer quantity,
        BigDecimal price,
        boolean availability,
        String productType,
        String ingredientType,
        boolean burgerIngredient,
        Integer productCategoryId,
        Integer supplierId,
        Integer createdBy
) {
}