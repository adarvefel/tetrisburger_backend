package com.tetris.tetrisburger_backend.domain.port.in.productcategory;

import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.query.GetProductCategoryByIdQuery;

public interface GetProductCategoryById {
    ProductCategory get(GetProductCategoryByIdQuery query);
}