package com.tetris.tetrisburger_backend.domain.port.in.burger;

import com.tetris.tetrisburger_backend.domain.port.in.burger.command.DeleteCustomBurgerCommand;

public interface DeleteCustomBurger {
    void handle(DeleteCustomBurgerCommand command);

}
