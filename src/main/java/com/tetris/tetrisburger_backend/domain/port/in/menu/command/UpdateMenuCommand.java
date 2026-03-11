package com.tetris.tetrisburger_backend.domain.port.in.menu.command;



import java.util.List;

public record UpdateMenuCommand(
        Integer idMenu,
        String name,
        String description,
        Boolean isAvailable,
        Integer idMenuCategory,
        List<MenuItemCommand> items,
        Integer updatedBy
) {}