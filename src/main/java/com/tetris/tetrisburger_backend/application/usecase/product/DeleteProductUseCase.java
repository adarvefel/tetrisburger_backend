package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.port.in.product.DeleteProduct;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;

public class DeleteProductUseCase implements DeleteProduct {
    private final ProductRepository productRepository;

    public DeleteProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void delete(Integer id) {
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        productRepository.deleteById(id);
    }
}