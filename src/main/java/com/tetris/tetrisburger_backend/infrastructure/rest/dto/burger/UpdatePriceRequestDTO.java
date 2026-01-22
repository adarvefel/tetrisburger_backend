package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger;

import java.math.BigDecimal;

public record UpdatePriceRequestDTO(
        BigDecimal newPrice
) {
}
