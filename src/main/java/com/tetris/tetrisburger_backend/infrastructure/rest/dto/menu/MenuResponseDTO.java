package com.tetris.tetrisburger_backend.infrastructure.rest.dto.menu;

import com.tetris.tetrisburger_backend.domain.common.ImageStatus;
import com.tetris.tetrisburger_backend.domain.model.MenuCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record MenuResponseDTO(
        Integer idMenu,
        String name,
        String description,
        boolean isAvailable,
        String imageUrl,
        ImageStatus imageStatus,
        MenuCategory menuCategory,
        List<MenuItemResponseDTO> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer createdBy,
        Integer updatedBy
) {}