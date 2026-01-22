package com.tetris.tetrisburger_backend.domain.port.in.product;

import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.command.CreateProductCommand;

public interface CreateProduct {
    Product create(CreateProductCommand cmd);
}