package com.tetris.tetrisburger_backend.domain.port.in.product;

import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.command.UpdateProductCommand;

public interface UpdateProduct {
    Product update(UpdateProductCommand cmd);
}