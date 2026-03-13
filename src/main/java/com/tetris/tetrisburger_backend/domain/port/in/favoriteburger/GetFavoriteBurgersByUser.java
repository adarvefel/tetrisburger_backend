package com.tetris.tetrisburger_backend.domain.port.in.favoriteburger;

import com.tetris.tetrisburger_backend.domain.model.FavoriteBurger;
import com.tetris.tetrisburger_backend.domain.model.FavoriteBurgerDetail;

import java.util.List;

public interface GetFavoriteBurgersByUser {
        List<FavoriteBurgerDetail> handle(Integer idUser);


}
