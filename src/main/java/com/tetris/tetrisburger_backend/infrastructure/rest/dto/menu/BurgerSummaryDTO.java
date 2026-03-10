package com.tetris.tetrisburger_backend.infrastructure.rest.dto.menu;

public record BurgerSummaryDTO(
        Integer idBurger,
        String name,
        String description,
        Double finalPrice,
        String imageUrl
) {}