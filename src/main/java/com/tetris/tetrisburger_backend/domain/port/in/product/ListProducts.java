package com.tetris.tetrisburger_backend.domain.port.in.product;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.query.ListProductsQuery;

public interface ListProducts {
    PageResponse<Product> list(ListProductsQuery query, PaginationRequest page);
}