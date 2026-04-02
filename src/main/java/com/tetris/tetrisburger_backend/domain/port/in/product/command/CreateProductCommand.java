package com.tetris.tetrisburger_backend.domain.port.in.product.command;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.enums.ProductType;

import java.math.BigDecimal;

public record CreateProductCommand(
        String name,
        String description,
        Integer quantity,
        BigDecimal price,
        Boolean availability,
        ProductType productType,
        Boolean isBurgerIngredient,
        FileData productImageData,
        Integer productCategoryId,
        Integer supplierId,
        Integer createdBy
) {}
