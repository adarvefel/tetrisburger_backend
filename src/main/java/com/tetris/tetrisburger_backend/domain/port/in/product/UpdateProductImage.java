package com.tetris.tetrisburger_backend.domain.port.in.product;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.model.Product;

public interface UpdateProductImage {
    Product update(Integer id, FileData productImage, Integer updatedBy);

}
