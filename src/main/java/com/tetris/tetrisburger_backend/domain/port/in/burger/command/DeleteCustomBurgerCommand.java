package com.tetris.tetrisburger_backend.domain.port.in.burger.command;

public record DeleteCustomBurgerCommand(
        Integer idBurger,
        Integer idUser
) {
}
