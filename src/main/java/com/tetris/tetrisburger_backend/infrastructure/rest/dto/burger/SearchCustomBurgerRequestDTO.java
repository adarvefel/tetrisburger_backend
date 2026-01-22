package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger;

public record SearchCustomBurgerRequestDTO(
        String name,
        int page,
        int size
) {
}
