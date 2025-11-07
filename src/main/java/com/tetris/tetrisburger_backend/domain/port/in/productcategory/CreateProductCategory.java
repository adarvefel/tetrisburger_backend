package com.tetris.tetrisburger_backend.domain.port.in.productcategory;

import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.command.CreateProductCategoryCommand;

public interface CreateProductCategory {
    ProductCategory create(CreateProductCategoryCommand cmd);
}
