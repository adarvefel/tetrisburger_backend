package com.tetris.tetrisburger_backend.domain.port.in.burger.user;


public interface UnmarkCustomBurgerAsFavorite {
    void handle(Integer idBurger, Integer idUser);
}
