package com.tetris.tetrisburger_backend.application.usecase.productcategory;


import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.ListPublicProductCategories;
import com.tetris.tetrisburger_backend.domain.port.out.ProductCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListPublicProductCategoriesUseCase implements ListPublicProductCategories {
    private final ProductCategoryRepository repo;

    public ListPublicProductCategoriesUseCase(ProductCategoryRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<ProductCategory> execute() {
        return repo.findPublicCategories();
    }
}