package com.tetris.tetrisburger_backend.infrastructure.rest.dto.menu;

import com.tetris.tetrisburger_backend.domain.common.ImageStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record MenuResponseDTO(
        Integer idMenu,
        String name,
        String description,
        BigDecimal regularPrice,
        BigDecimal comboPrice,
        boolean isAvailable,
        String imageUrl,
        ImageStatus imageStatus,
        Integer idMenuCategory,
        List<MenuItemResponseDTO> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}