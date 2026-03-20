package com.tetris.tetrisburger_backend.domain.port.in.product;

import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.query.GetProductByIdQuery;

public interface GetProductById {
    Product get(GetProductByIdQuery query);
}