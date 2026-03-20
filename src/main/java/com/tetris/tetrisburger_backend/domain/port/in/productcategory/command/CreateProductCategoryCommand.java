package com.tetris.tetrisburger_backend.domain.port.in.productcategory.command;

public record CreateProductCategoryCommand(
        String name,
        String description,
        Boolean available
) {
}