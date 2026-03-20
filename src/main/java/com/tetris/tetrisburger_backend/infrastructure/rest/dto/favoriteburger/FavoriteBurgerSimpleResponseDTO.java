package com.tetris.tetrisburger_backend.infrastructure.rest.dto.favoriteburger;

import java.time.LocalDateTime;

public record FavoriteBurgerSimpleResponseDTO(
        Integer idFavorite,
        Integer idUser,
        Integer idBurger,
        String name,
        LocalDateTime createdAt
) {}