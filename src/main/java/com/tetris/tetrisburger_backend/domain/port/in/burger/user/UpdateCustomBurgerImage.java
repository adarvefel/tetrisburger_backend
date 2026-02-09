package com.tetris.tetrisburger_backend.domain.port.in.burger.user;

import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateCustomBurgerImageCommand;

public interface UpdateCustomBurgerImage {
    Burger handle(UpdateCustomBurgerImageCommand command);
}
