package com.tetris.tetrisburger_backend.domain.port.in.menucategory.command;

public record UpdateMenuCategoryCommand(
        String menuCategoryName,
        String  description,
        Integer updatedBy
) {
}
