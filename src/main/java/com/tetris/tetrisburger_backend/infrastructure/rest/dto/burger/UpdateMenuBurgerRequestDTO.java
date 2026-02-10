package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record UpdateMenuBurgerRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String name,

        @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
        String description,
        @NotNull(message = "Debe especificar la disponibilidad")
        Boolean availability,

        @NotEmpty(message = "Debe incluir al menos un ingrediente")
        @Size(min = 1, max = 15, message = "Una hamburguesa debe tener entre 1 y 15 ingredientes")
        @Valid
        List<IngredientRequestDTO> ingredients




) {}
