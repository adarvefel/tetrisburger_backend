package com.tetris.tetrisburger_backend.application.usecase.productcategory;

import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.GetProductCategoryById;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.query.GetProductCategoryByIdQuery;
import com.tetris.tetrisburger_backend.domain.port.out.ProductCategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class GetProductCategoryByIdUseCase implements GetProductCategoryById {
    private final ProductCategoryRepository repo;

    public GetProductCategoryByIdUseCase(ProductCategoryRepository repo) {
        this.repo = repo;
    }

    @Override
    public ProductCategory get(GetProductCategoryByIdQuery query) {
        return repo.findById(query.id())
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + query.id()));
    }
}
