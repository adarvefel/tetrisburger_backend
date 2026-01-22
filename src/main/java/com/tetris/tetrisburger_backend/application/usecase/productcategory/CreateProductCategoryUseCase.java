package com.tetris.tetrisburger_backend.application.usecase.productcategory;

import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.CreateProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.command.CreateProductCategoryCommand;
import com.tetris.tetrisburger_backend.domain.port.out.ProductCategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CreateProductCategoryUseCase implements CreateProductCategory {

    private final ProductCategoryRepository repo;

    public CreateProductCategoryUseCase(ProductCategoryRepository repo) {
        this.repo = repo;
    }

    @Override
    public ProductCategory create(CreateProductCategoryCommand cmd) {
        if (repo.existsByNameIgnoreCase(cmd.name().trim())) {
            throw new IllegalArgumentException("Product category already exists: " + cmd.name());
        }
        Boolean avail = cmd.available() != null ? cmd.available() : Boolean.TRUE;
        ProductCategory cat = ProductCategory.ofNew(cmd.name().trim(), cmd.description(), avail);
        return repo.save(cat);
    }
}