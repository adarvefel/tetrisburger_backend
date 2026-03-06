package com.tetris.tetrisburger_backend.domain.port.in.menu.command;



import java.math.BigDecimal;
import java.util.List;

public record UpdateMenuCommand(
        Integer idMenu,
        String name,
        String description,
        BigDecimal regularPrice,
        BigDecimal comboPrice,
        boolean isAvailable,
        String imageUrl,
        String imageKey,
        Integer idMenuCategory,
        List<MenuItemCommand> items,
        Integer updatedBy
) {}