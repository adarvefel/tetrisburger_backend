package com.tetris.tetrisburger_backend.domain.port.in.burger;

import com.tetris.tetrisburger_backend.domain.model.Burger;

public interface ToggleMenuBurgerFavorite {
    Burger handle(Integer idBurger, Boolean isFavorite, Integer adminUserId);

}
