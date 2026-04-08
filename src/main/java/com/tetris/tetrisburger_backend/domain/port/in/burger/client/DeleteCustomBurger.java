package com.tetris.tetrisburger_backend.domain.port.in.burger.client;


import com.tetris.tetrisburger_backend.domain.port.in.burger.client.command.DeleteCustomBurgerCommand;

public interface DeleteCustomBurger {
    void handle(DeleteCustomBurgerCommand command);
}
