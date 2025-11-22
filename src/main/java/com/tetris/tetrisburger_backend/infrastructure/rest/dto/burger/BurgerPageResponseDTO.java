package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger;

import java.util.List;

public record BurgerPageResponseDTO(
        List<BurgerResponseDTO> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
}
