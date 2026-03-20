package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.admin;

import java.util.List;

public record MenuBurgerPageResponseDTO(
        List<MenuBurgerResponseDTO> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
