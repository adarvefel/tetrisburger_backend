package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.burger.SearchIngredients;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SearchIngredientsUseCase implements SearchIngredients {

    private final ProductRepository productRepository;

    public SearchIngredientsUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public PageResponse<Product> handle(String name, PaginationRequest request) {
        return productRepository.searchIngredients(name, request);
    }
}