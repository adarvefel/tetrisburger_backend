package com.tetris.tetrisburger_backend.domain.port.in.productcategory.command;

public record UpdateProductCategoryCommand(
        Integer id,
        String name,
        String description,
        Boolean available
) {
}
