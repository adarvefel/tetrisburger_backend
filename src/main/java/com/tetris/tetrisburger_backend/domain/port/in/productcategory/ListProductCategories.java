package com.tetris.tetrisburger_backend.domain.port.in.productcategory;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.query.ListProductCategoriesQuery;

public interface ListProductCategories {
    PageResponse<ProductCategory> list(ListProductCategoriesQuery query, PaginationRequest page);
}
