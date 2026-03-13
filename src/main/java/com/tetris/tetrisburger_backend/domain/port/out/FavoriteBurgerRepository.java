package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.model.FavoriteBurger;

import java.util.List;

public interface FavoriteBurgerRepository {
    FavoriteBurger save(FavoriteBurger favorite);
    void deleteByUserAndBurger(Integer idUser, Integer idBurger);
    boolean existsByUserAndBurger(Integer idUser, Integer idBurger);
    List<FavoriteBurger> findAllByUser(Integer idUser);
}
