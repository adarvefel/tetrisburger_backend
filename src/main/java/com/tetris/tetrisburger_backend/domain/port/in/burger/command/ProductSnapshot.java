package com.tetris.tetrisburger_backend.domain.port.in.burger.command;

import com.tetris.tetrisburger_backend.domain.model.Product;

import java.math.BigDecimal;

public record ProductSnapshot(
        Integer productId,
        String name,
        BigDecimal price,
        String ingredientType,
        Integer quantity,
        Boolean isOptional
) {

    public static ProductSnapshot fromProduct(Product product, Integer quantity, Boolean isOptional) {
        if (product == null) {
            throw new IllegalArgumentException("El producto no puede ser null");
        }
        if (!Boolean.TRUE.equals(product.getBurgerIngredient())) {
            throw new IllegalArgumentException(
                    "El producto '" + product.getName() + "' no es un ingrediente de hamburguesa"
            );
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        return new ProductSnapshot(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getIngredientType(),
                quantity,
                isOptional
        );
    }
}
