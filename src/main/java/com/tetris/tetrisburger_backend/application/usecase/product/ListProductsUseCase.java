package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.ListProducts;
import com.tetris.tetrisburger_backend.domain.port.in.product.query.ListProductsQuery;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;

public class ListProductsUseCase implements ListProducts {
    private final ProductRepository productRepository;

    public ListProductsUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public PageResponse<Product> list(ListProductsQuery query, PaginationRequest page) {
        return productRepository.findAll(query.productCategoryId(), query.availability(), page);
    }
}