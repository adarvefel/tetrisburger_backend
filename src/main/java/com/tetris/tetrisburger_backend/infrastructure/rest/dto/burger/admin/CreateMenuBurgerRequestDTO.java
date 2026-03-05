package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.admin;

import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.IngredientRequestDTO;
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
        @Positive(message = "El precio debe ser mayor a cero")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
        @DecimalMax(value = "999999.99", message = "El precio no puede exceder 999,999.99")
        BigDecimal finalPrice,


        Boolean isFeatured,

        @NotNull(message = "Debe especificar la disponibilidad")
        Boolean availability,

        @NotEmpty(message = "Debe incluir al menos un ingrediente")
        @Valid
        List<IngredientRequestDTO> ingredients

         )
{}
