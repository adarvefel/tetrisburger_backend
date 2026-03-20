package com.tetris.tetrisburger_backend.domain.port.in.burger.client;

import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.client.command.CreateCustomBurgerCommand;

public interface CreateCustomBurger {
    Burger handle(CreateCustomBurgerCommand command);
}
