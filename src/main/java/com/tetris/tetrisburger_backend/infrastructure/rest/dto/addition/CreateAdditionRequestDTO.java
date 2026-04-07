package com.tetris.tetrisburger_backend.infrastructure.rest.dto.addition;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateAdditionRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
        String name,

        String description,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.00", inclusive = true, message = "El precio no puede ser negativo")
        @Digits(integer = 10, fraction = 2, message = "Formato de precio inválido")
        BigDecimal price,

        Boolean available
) {
    public CreateAdditionRequestDTO(String name, double price, Boolean available) {
        this(name, null, BigDecimal.valueOf(price), available);
    }
}
