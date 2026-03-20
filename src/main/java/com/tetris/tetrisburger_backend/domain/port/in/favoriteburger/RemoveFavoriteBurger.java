package com.tetris.tetrisburger_backend.domain.port.in.favoriteburger;

public interface RemoveFavoriteBurger {
    void handle(Integer idUser, Integer idBurger);
}
