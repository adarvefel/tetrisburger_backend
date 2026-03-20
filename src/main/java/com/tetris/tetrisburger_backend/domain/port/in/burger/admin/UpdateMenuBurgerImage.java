package com.tetris.tetrisburger_backend.domain.port.in.burger.admin;

import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateMenuBurgerImageCommand;

public interface UpdateMenuBurgerImage {
    Burger handle(UpdateMenuBurgerImageCommand command);
}
