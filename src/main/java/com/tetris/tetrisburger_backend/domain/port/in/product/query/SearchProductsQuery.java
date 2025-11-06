package com.tetris.tetrisburger_backend.domain.port.in.product.query;

public record SearchProductsQuery(
        String q,
        Integer productCategoryId,
        Boolean availability
) {
}
