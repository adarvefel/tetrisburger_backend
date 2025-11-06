package com.tetris.tetrisburger_backend.domain.port.in.product.query;

public record ListProductsQuery(
        Integer productCategoryId,
        Boolean availability
) {
}