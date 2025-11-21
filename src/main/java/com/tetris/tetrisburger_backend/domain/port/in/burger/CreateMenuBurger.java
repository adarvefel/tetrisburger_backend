package com.tetris.tetrisburger_backend.domain.port.in.burger;

import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.CreateBurgerCommand;

public interface CreateMenuBurger{
    Burger handle(CreateBurgerCommand command);


}
