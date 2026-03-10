package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.client;

public record SearchCustomBurgerRequestDTO(
        String name,
        int page,
        int size
) {
}
