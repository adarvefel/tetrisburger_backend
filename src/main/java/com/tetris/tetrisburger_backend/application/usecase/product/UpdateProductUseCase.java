// src/main/java/com/tetris/tetrisburger_backend/application/usecase/product/UpdateProductUseCase.java
package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.UpdateProduct;
import com.tetris.tetrisburger_backend.domain.port.in.product.command.UpdateProductCommand;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Transactional
public class UpdateProductUseCase implements UpdateProduct {

    private static final Logger logger = LoggerFactory.getLogger(UpdateProductUseCase.class);

    private final ProductRepository repo;

    public UpdateProductUseCase(ProductRepository repo) {
        this.repo = repo;
    }

    @Override
    public Product update(UpdateProductCommand cmd) {
        logger.info("Actualizando producto ID: {}", cmd.idProduct());

        Product current = repo.findById(cmd.idProduct())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + cmd.idProduct()));

        // Reconstruir producto con datos actualizados
        // IMPORTANTE: Mantener imageUrl e imageKey del producto actual (no se actualizan aquí)
        Product updated = Product.of(
                current.getId(),
                cmd.name(),
                cmd.description(),
                cmd.quantity(),
                cmd.price(),
                cmd.availability(),
                cmd.productType(),
                cmd.ingredientType(),
                cmd.burgerIngredient(),
                current.getImageUrl(),
                current.getImageKey(),
                cmd.productCategoryId(),
                cmd.supplierId(),
                current.getCreatedAt(),
                Instant.now(),             // ← updatedAt
                current.getDeletedAt(),
                current.getCreatedBy(),
                cmd.updatedBy(),
                current.getDeletedBy()
        );

        return repo.save(updated);
    }
}
