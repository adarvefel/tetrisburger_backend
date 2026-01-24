package com.tetris.tetrisburger_backend.infrastructure.rest.dto.menu;

import jakarta.validation.constraints.NotBlank;

public record CreateMenuCategoryRequestDTO(
        String menuCategoryName,
        String description

        ) {
}
