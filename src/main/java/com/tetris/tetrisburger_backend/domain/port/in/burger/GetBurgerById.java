package com.tetris.tetrisburger_backend.domain.port.in.burger;

import com.tetris.tetrisburger_backend.domain.model.Burger;

import java.util.Optional;

public interface GetBurgerById {
    Burger execute(Integer idBurger);
}
