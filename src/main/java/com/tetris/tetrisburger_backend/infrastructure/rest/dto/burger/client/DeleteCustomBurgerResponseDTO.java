package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.client;

public record DeleteCustomBurgerResponseDTO(
        Integer idBurger,
        String message,
        Boolean success,
        Long timestamp
) {
}
