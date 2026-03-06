package com.tetris.tetrisburger_backend.infrastructure.rest.dto.menucategory;

import java.time.LocalDateTime;

public record MenuCategoryResponseDTO(
        Integer idMenuCategory,
        String menuCategoryName,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}