package com.tetris.tetrisburger_backend.domain.port.in.burger.command;

import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.model.ProductType;

import java.math.BigDecimal;

public record ProductSnapshot(
        Integer idProduct,
        String name,
        BigDecimal price,
        String categoryName,
        Integer quantity
) {

    public static ProductSnapshot fromProduct(Product product, Integer quantity) {
        if (product == null) {
            throw new IllegalArgumentException("El producto no puede ser null");
        }

        if (product.getProductType() != ProductType.INGREDIENT) {
            throw new IllegalArgumentException(
                    "El producto '" + product.getName() + "' no es un ingrediente de hamburguesa. " +
                            "Tipo actual: " + product.getProductType()
            );
        }

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor a 0. Cantidad recibida: " + quantity
            );
        }

        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El producto '" + product.getName() + "' no tiene un precio válido"
            );
        }


        return new ProductSnapshot(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getProductCategory() != null ? product.getProductCategory().getName() : "Sin categoría",
                quantity
        );
    }

    public BigDecimal calculateSubtotal() {
        if (price == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return price.multiply(new BigDecimal(quantity));
    }
}
