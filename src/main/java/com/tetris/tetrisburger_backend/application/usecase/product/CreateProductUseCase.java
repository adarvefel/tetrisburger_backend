package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.CreateProduct;
import com.tetris.tetrisburger_backend.domain.port.in.product.command.CreateProductCommand;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;

public class CreateProductUseCase implements CreateProduct {
    private final ProductRepository productRepository;

    public CreateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product create(CreateProductCommand cmd) {
        if (productRepository.existsByNameIgnoreCase(cmd.name())) {
            throw new IllegalArgumentException("Product name already exists: " + cmd.name());
        }
        Product product = Product.ofNew(
                cmd.name(), cmd.description(), cmd.quantity(), cmd.price(),
                cmd.availability(), cmd.productType(), cmd.ingredientType(), cmd.burgerIngredient(),
                cmd.productCategoryId(), cmd.supplierId(), cmd.createdBy()
        );
        return productRepository.save(product);
    }
}