package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.AdjustProductStock;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Transactional
public class AdjustProductStockUseCase implements AdjustProductStock {
    private final ProductRepository productRepository;

    public AdjustProductStockUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product adjustStock(Integer id, int delta, Integer updatedBy) {
        Product current = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        int newQty = Math.max(0, (current.getQuantity() == null ? 0 : current.getQuantity()) + delta);
        Product updated = new Product(
                current.getId(),
                current.getName(),
                current.getDescription(),
                newQty,
                current.getPrice(),
                current.isAvailability(),
                current.getProductType(),
                current.getIngredientType(),
                current.isBurgerIngredient(),
                current.getProductCategoryId(),
                current.getSupplierId(),
                current.getCreatedAt(),
                Instant.now(),
                current.getDeletedAt(),
                current.getCreatedBy(),
                updatedBy,
                current.getDeletedBy()
        );
        return productRepository.save(updated);
    }
}