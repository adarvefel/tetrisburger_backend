package com.tetris.tetrisburger_backend.infrastructure.rest.dto.menu;

import java.math.BigDecimal;

public record ProductSummaryDTO(
        Integer idProduct,
        String name,
        BigDecimal price,
        String imageUrl
) {}