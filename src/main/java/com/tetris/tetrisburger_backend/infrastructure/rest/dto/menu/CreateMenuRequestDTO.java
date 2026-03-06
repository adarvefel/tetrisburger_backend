package com.tetris.tetrisburger_backend.infrastructure.rest.dto.menu;


import java.math.BigDecimal;
import java.util.List;

public record CreateMenuRequestDTO(
        String name,
        String description,
        BigDecimal regularPrice,
        BigDecimal comboPrice,
        Boolean isAvailable,
        Integer idMenuCategory,
        List<MenuItemRequestDTO> items
) {}