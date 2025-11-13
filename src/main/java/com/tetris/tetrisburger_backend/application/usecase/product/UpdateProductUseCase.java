// src/main/java/com/tetris/tetrisburger_backend/application/usecase/product/UpdateProductUseCase.java
package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.UpdateProduct;
import com.tetris.tetrisburger_backend.domain.port.in.product.command.UpdateProductCommand;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UpdateProductUseCase implements UpdateProduct {
    private final ProductRepository repo;

    public UpdateProductUseCase(ProductRepository repo) {
        this.repo = repo;
    }

    @Override
    public Product update(UpdateProductCommand cmd) {
        Product current = repo.findById(cmd.idProduct())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + cmd.idProduct()));
        // reconstruir o actualizar según tu modelo; si Product es inmutable, delega en un factory/update
        Product updated = Product.of(
                current.getId(), cmd.name(), cmd.description(), cmd.quantity(), cmd.price(),
                cmd.availability(), cmd.productType(), cmd.ingredientType(), cmd.burgerIngredient(),
                cmd.imageUrl(), cmd.productCategoryId(), cmd.supplierId(),
                current.getCreatedAt(), current.getUpdatedAt(), current.getDeletedAt(),
                current.getCreatedBy(), cmd.updatedBy(), current.getDeletedBy()
        );
        return repo.save(updated);
    }
}
