package com.tetris.tetrisburger_backend.infrastructure.rest.dto.menu;

public record MenuItemRequestDTO(
        String itemType,
        Integer idBurger,
        Integer idProduct,
        Integer quantity
) {}