package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.admin;

import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

public record UpdatePriceRequestDTO(
        @Digits(integer = 6, fraction = 2, message = "El precio debe tener máximo 6 dígitos enteros y 2 decimales")
        BigDecimal newPrice


) {
}
