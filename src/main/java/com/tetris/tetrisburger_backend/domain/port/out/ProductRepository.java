package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Product;

import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);

    Optional<Product> findById(Integer id);



    void deleteById(Integer id);

    boolean existsByNameIgnoreCase(String name);

    PageResponse<Product> findAll(Integer productCategoryId, Boolean availability, PaginationRequest page);

    PageResponse<Product> search(String q, Integer productCategoryId, Boolean availability, PaginationRequest page);
}