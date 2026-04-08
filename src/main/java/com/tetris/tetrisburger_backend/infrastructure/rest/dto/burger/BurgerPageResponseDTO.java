package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger;

import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.client.BurgerResponseDTO;

import java.util.List;

public record BurgerPageResponseDTO(
        List<BurgerResponseDTO> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
