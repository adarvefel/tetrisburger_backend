package com.tetris.tetrisburger_backend.domain.port.in.productcategory;

import com.tetris.tetrisburger_backend.domain.model.ProductCategory;

import java.util.List;

public interface ListPublicProductCategories {
    List<ProductCategory> execute();
}