package com.tetris.tetrisburger_backend.infrastructure.rest.dto.menucategory;

public record UpdateMenuCategoryRequestDTO(
        String menuCategoryName,
        String description
) {
}
