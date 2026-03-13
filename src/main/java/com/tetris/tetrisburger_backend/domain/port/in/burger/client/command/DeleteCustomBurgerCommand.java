package com.tetris.tetrisburger_backend.domain.port.in.burger.client.command;

public record DeleteCustomBurgerCommand(
        Integer idBurger,
        Integer idUser
) {
}
