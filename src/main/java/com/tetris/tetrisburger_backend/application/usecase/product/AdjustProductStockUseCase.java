package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.exception.ProductAlreadyDeletedException;
import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
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

        if (delta == 0) {
            throw new IllegalArgumentException("El numero de stock no puede ser 0");
        }

        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        product.adjustStock(delta, updatedBy);

        return productRepository.save(product);
    }

}
