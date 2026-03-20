package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.model.ProductType;
import com.tetris.tetrisburger_backend.domain.port.in.product.ListPublicProducts;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ListPublicProductsUseCase implements ListPublicProducts {

    private final ProductRepository productRepository;

    public ListPublicProductsUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public PageResponse<Product> list(ProductType productType, Integer categoryId, PaginationRequest page) {
        return productRepository.findPublicProducts(productType, categoryId, page);
    }
}