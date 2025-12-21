package com.tetris.tetrisburger_backend.infrastructure.rest.dto.menu;

import java.time.LocalDateTime;

public record CreateMenuCategoryResponseDTO(
        Integer idMenuCategory,
        String menuCategoryName,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        Integer createdBy,
        Integer updatedBy,
        Integer deletedBy


) {
}
