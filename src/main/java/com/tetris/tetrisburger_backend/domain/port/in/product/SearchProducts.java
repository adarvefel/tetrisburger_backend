package com.tetris.tetrisburger_backend.domain.port.in.product;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.query.SearchProductsQuery;

public interface SearchProducts {
    PageResponse<Product> search(SearchProductsQuery query, PaginationRequest page);
}