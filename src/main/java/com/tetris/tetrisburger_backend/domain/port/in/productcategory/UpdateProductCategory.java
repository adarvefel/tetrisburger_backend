package com.tetris.tetrisburger_backend.domain.port.in.productcategory;

import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.command.UpdateProductCategoryCommand;

public interface UpdateProductCategory {
    ProductCategory update(UpdateProductCategoryCommand cmd);
}