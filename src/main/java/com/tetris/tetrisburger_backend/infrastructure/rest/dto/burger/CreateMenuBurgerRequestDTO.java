package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record CreateMenuBurgerRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String name,

        @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
        String description,

        @NotEmpty(message = "Debe incluir al menos un ingrediente")
        @Size(min = 1, max = 15, message = "Una hamburguesa debe tener entre 1 y 15 ingredientes")
        @Valid
        List<IngredientRequestDTO> ingredients,

        @Positive(message = "El precio debe ser mayor a cero")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
        @DecimalMax(value = "999999.99", message = "El precio no puede exceder 999,999.99")
        BigDecimal finalPrice,

        @NotNull(message = "Debe especificar si es destacada")
        Boolean isFavorite,

        @NotNull(message = "Debe especificar la disponibilidad")
        Boolean availability  )
{}
