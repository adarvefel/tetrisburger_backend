package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.client;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CustomIngredientRequestDTO(

        @NotNull(message = "El ID del producto es obligatorio")
        Integer idProduct,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        Integer quantity

) {}