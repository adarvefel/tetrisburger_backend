package com.tetris.tetrisburger_backend.application.usecase.productcategory;

import com.tetris.tetrisburger_backend.domain.port.in.productcategory.DeleteProductCategory;
import com.tetris.tetrisburger_backend.domain.port.out.ProductCategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DeleteProductCategoryUseCase implements DeleteProductCategory {
    private final ProductCategoryRepository repo;

    public DeleteProductCategoryUseCase(ProductCategoryRepository repo) {
        this.repo = repo;
    }

    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
    }
}
