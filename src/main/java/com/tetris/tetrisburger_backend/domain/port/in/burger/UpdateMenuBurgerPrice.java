package com.tetris.tetrisburger_backend.domain.port.in.burger;

import com.tetris.tetrisburger_backend.domain.model.Burger;

import java.math.BigDecimal;

public interface UpdateMenuBurgerPrice {
    Burger handle(Integer idBurger, BigDecimal newPrice);

}
