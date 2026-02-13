package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.user;

public record SearchCustomBurgerRequestDTO(
        String name,
        int page,
        int size
) {
}
