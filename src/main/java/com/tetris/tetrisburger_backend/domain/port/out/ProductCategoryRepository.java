package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.ProductCategory;

import java.util.Optional;

public interface ProductCategoryRepository {
    ProductCategory save(ProductCategory category);

    Optional<ProductCategory> findById(Integer id);

    void deleteById(Integer id);

    boolean existsByNameIgnoreCase(String name);

    PageResponse<ProductCategory> findAll(String nameContains, PaginationRequest page);
}