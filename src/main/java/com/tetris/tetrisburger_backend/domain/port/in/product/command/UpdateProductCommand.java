package com.tetris.tetrisburger_backend.domain.port.in.product.command;

import com.tetris.tetrisburger_backend.domain.model.ProductType;

import java.math.BigDecimal;

public record UpdateProductCommand(
        Integer idProduct,
        String name,
        String description,
        Integer quantity,
        BigDecimal price,
        Boolean availability,
        ProductType productType,
        Integer productCategoryId,
        Integer supplierId,
        Integer updatedBy
) {}
