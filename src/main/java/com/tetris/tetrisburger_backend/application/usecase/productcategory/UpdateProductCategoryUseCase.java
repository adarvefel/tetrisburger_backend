package com.tetris.tetrisburger_backend.application.usecase.productcategory;

import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.UpdateProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.command.UpdateProductCategoryCommand;
import com.tetris.tetrisburger_backend.domain.port.out.ProductCategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UpdateProductCategoryUseCase implements UpdateProductCategory {

    private final ProductCategoryRepository repo;

    public UpdateProductCategoryUseCase(ProductCategoryRepository repo) {
        this.repo = repo;
    }

    @Override
    public ProductCategory update(UpdateProductCategoryCommand cmd) {
        ProductCategory current = repo.findById(cmd.id())
                .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada: " + cmd.id()));
        current.update(cmd.name().trim(), cmd.description(), cmd.available(),cmd.updatedBy());
        return repo.save(current);
    }
}