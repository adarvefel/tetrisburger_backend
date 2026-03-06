package com.tetris.tetrisburger_backend.domain.port.in.menu.command;

public record MenuItemCommand(
        String itemType,
        Integer idBurger,
        Integer idProduct,
        Integer quantity
) {}