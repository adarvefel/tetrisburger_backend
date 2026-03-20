package com.tetris.tetrisburger_backend.application.usecase.productcategory;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.ListProductCategories;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.query.ListProductCategoriesQuery;
import com.tetris.tetrisburger_backend.domain.port.out.ProductCategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class ListProductCategoriesUseCase implements ListProductCategories {
    private final ProductCategoryRepository repo;

    public ListProductCategoriesUseCase(ProductCategoryRepository repo) {
        this.repo = repo;
    }

    @Override
    public PageResponse<ProductCategory> list(ListProductCategoriesQuery query, PaginationRequest page) {
        return repo.findAll(query.nameContains(), page);
    }
}
