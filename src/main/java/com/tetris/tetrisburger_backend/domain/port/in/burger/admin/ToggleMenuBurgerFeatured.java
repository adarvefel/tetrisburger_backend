package com.tetris.tetrisburger_backend.domain.port.in.burger.admin;

import com.tetris.tetrisburger_backend.domain.model.Burger;

public interface ToggleMenuBurgerFeatured {
    Burger handle(Integer idBurger, Boolean isFeatured, Integer adminUserId);

}
