package com.tetris.tetrisburger_backend.domain.port.in.favoriteburger;

import com.tetris.tetrisburger_backend.domain.model.FavoriteBurger;

public interface AddFavoriteBurger {
    FavoriteBurger handle(Integer idUser, Integer idBurger);
}
