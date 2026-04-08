package com.tetris.tetrisburger_backend.domain.port.in.product;

import com.tetris.tetrisburger_backend.domain.model.Product;

public interface AdjustProductStock {
    Product adjustStock(Integer id, int delta, Integer updatedBy);
}