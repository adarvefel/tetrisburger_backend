package com.tetris.tetrisburger_backend.domain.port.in.product;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.enums.ProductType;

public interface ListPublicProducts {
    PageResponse<Product> list(ProductType productType, Integer categoryId, PaginationRequest page);
}