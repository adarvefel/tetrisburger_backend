package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.UpdateProduct;
import com.tetris.tetrisburger_backend.domain.port.in.product.command.UpdateProductCommand;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;

import java.time.Instant;

public class UpdateProductUseCase implements UpdateProduct {
    private final ProductRepository productRepository;

    public UpdateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product update(UpdateProductCommand cmd) {
        Product current = productRepository.findById(cmd.id())
                .orElseThrow(() -> new ProductNotFoundException(cmd.id()));

        Product updated = new Product(
                current.getId(),
                cmd.name() != null ? cmd.name() : current.getName(),
                cmd.description() != null ? cmd.description() : current.getDescription(),
                cmd.quantity() != null ? cmd.quantity() : current.getQuantity(),
                cmd.price() != null ? cmd.price() : current.getPrice(),
                cmd.availability() != null ? cmd.availability() : current.isAvailability(),
                cmd.productType() != null ? cmd.productType() : current.getProductType(),
                cmd.ingredientType() != null ? cmd.ingredientType() : current.getIngredientType(),
                cmd.burgerIngredient() != null ? cmd.burgerIngredient() : current.isBurgerIngredient(),
                cmd.productCategoryId() != null ? cmd.productCategoryId() : current.getProductCategoryId(),
                cmd.supplierId() != null ? cmd.supplierId() : current.getSupplierId(),
                current.getCreatedAt(),
                Instant.now(),
                current.getDeletedAt(),
                current.getCreatedBy(),
                cmd.updatedBy() != null ? cmd.updatedBy() : current.getUpdatedBy(),
                current.getDeletedBy()
        );
        return productRepository.save(updated);
    }
}
