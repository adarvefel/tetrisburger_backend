package com.tetris.tetrisburger_backend.infrastructure.rest.dto.favoriteburger;

import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.client.BurgerResponseDTO;

import java.time.LocalDateTime;

public record FavoriteBurgerResponseDTO(
        Integer idFavorite,
        Integer idUser,
        BurgerResponseDTO burger,
        LocalDateTime createdAt
) {}
