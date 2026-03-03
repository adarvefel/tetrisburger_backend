package com.tetris.tetrisburger_backend.domain.port.in.burger.command;

import com.tetris.tetrisburger_backend.domain.common.FileData;

import java.util.List;

public record CreateCustomBurgerCommand(
        String name,
        String description,
        FileData imageData,
        Integer createdBy,
        List<IngredientRequest> ingredients
) {
    public record IngredientRequest(
            Integer idProduct,
            Integer quantity
    ) {}
}
