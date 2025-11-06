package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.SetProductAvailability;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;

import java.time.Instant;

public class SetProductAvailabilityUseCase implements SetProductAvailability {
    private final ProductRepository productRepository;

    public SetProductAvailabilityUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product setAvailability(Integer id, boolean availability, Integer updatedBy) {
        Product current = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        Product updated = new Product(
                current.getId(),
                current.getName(),
                current.getDescription(),
                current.getQuantity(),
                current.getPrice(),
                availability,
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