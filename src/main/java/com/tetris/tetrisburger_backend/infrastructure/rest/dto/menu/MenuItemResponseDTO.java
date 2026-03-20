package com.tetris.tetrisburger_backend.infrastructure.rest.dto.menu;


public record MenuItemResponseDTO(
        Integer idMenuItem,
        String itemType,
        BurgerSummaryDTO burger,
        ProductSummaryDTO product,
        Integer quantity
) {}