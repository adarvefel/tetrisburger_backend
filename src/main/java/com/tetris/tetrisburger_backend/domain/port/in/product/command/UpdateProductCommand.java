// src/main/java/com/tetris/tetrisburger_backend/domain/port/in/product/command/UpdateProductCommand.java
package com.tetris.tetrisburger_backend.domain.port.in.product.command;

import java.math.BigDecimal;

public record UpdateProductCommand(
        Integer idProduct,
        String name,
        String description,
        Integer quantity,
        BigDecimal price,
        Boolean availability,
        String productType,
        String ingredientType,
        Boolean burgerIngredient,
        String imageUrl,              // nuevo
        Integer productCategoryId,
        Integer supplierId,
        Integer updatedBy
) {
}
