package com.tetris.tetrisburger_backend.infrastructure.rest.dto.menucategory;

public record CreateMenuCategoryRequestDTO(
        String menuCategoryName,
        String description

        ) {
}
