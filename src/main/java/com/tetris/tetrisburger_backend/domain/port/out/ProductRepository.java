package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Product;

import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);

    Optional<Product> findById(Integer id);

    void deleteById(Integer id);

    PageResponse<Product> findAllBurgerIngredients(Integer categoryId, PaginationRequest pagination);


    boolean existsByNameIgnoreCaseAndDeletedAtIsNull(String name);
    boolean existsByNameIgnoreCaseAndDeletedAtIsNullAndIdNot(String name, Integer id);

    PageResponse<Product> searchIngredients(String name, PaginationRequest pagination);


    PageResponse<Product> findAll(Integer productCategoryId, Boolean availability, PaginationRequest page);

    PageResponse<Product> search(String q, Integer productCategoryId, Boolean availability, PaginationRequest page);
}