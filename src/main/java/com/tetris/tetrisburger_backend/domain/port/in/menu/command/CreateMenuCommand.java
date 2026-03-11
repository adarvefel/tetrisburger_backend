package com.tetris.tetrisburger_backend.domain.port.in.menu.command;

import com.tetris.tetrisburger_backend.domain.common.FileData;

import java.math.BigDecimal;
import java.util.List;

public record CreateMenuCommand(
        String name,
        String description,
        Boolean isAvailable,
        Integer idMenuCategory,
        List<MenuItemCommand> items,
        FileData imageData,
        Integer createdBy
) {}