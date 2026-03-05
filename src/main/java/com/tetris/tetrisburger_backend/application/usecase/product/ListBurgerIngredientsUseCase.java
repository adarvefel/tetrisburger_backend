package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.ListBurgerIngredients;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ListBurgerIngredientsUseCase implements ListBurgerIngredients {

    private final ProductRepository productRepository;

    public ListBurgerIngredientsUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public PageResponse<Product> handle(Integer categoryId, PaginationRequest pagination) {
        return productRepository.findAllBurgerIngredients(categoryId, pagination);
    }
}
