package com.tetris.tetrisburger_backend.domain.port.in.product.query;

import com.tetris.tetrisburger_backend.domain.model.ProductType;

public record SearchProductsQuery(
        String q,
        Integer productCategoryId,
        Boolean availability,
        ProductType productType
) {
    public SearchProductsQuery() {
        this(null, null, null,null);
    }

    public SearchProductsQuery(String q, Integer productCategoryId, Boolean availability) {
        this(q, productCategoryId, availability, null);
    }
}
