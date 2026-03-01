package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.exception.ProductAlreadyDeletedException;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.AdjustProductStock;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
@Transactional
public class AdjustProductStockUseCase implements AdjustProductStock {

    private final ProductRepository productRepository;

    public AdjustProductStockUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product adjustStock(Integer productId, int delta, Integer updatedBy) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductAlreadyDeletedException(productId));

        int currentQuantity = product.getQuantity();

        product.adjustStock(delta, updatedBy);

        Product updated = productRepository.save(product);

        return updated;
    }

}
