package com.tetris.tetrisburger_backend.domain.port.in.burger.command;

import com.tetris.tetrisburger_backend.domain.common.FileData;

import java.math.BigDecimal;
import java.util.List;

public record CreateBurgerCommand(
        String name,
        String description,
        FileData imageData,
        List<IngredientRequest> ingredients,
        boolean isFavorite,
        BigDecimal finalPrice,
        Integer createdBy
) {
    public record IngredientRequest(
            Integer idProduct,
            Integer quantity,
            Boolean isOptional
    ) {

    }
}
