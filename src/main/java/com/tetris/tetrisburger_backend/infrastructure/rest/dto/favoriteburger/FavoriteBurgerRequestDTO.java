package com.tetris.tetrisburger_backend.infrastructure.rest.dto.favoriteburger;

import jakarta.validation.constraints.NotNull;

public record FavoriteBurgerRequestDTO(
        @NotNull(message = "El id de la hamburguesa es obligatorio")
        Integer idBurger
) {}
