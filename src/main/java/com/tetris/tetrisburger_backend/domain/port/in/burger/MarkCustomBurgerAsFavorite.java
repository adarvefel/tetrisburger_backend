package com.tetris.tetrisburger_backend.domain.port.in.burger;

public interface MarkCustomBurgerAsFavorite {
    void handle(Integer idBurger, Integer idUser);

}
