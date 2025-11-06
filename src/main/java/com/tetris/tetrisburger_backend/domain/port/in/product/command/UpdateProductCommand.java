package com.tetris.tetrisburger_backend.domain.port.in.product.command;

import java.math.BigDecimal;

public record UpdateProductCommand(
        Integer id,
        String name,
        String description,
        Integer quantity,
        BigDecimal price,
        Boolean availability,
        String productType,
        String ingredientType,
        Boolean burgerIngredient,
        Integer productCategoryId,
        Integer supplierId,
        Integer updatedBy
) {
}