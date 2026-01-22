package com.tetris.tetrisburger_backend.domain.port.in.product;

import com.tetris.tetrisburger_backend.domain.model.Product;

public interface SetProductAvailability {
    Product setAvailability(Integer id, boolean availability, Integer updatedBy);
}