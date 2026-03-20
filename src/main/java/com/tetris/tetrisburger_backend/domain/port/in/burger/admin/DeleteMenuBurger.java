package com.tetris.tetrisburger_backend.domain.port.in.burger.admin;

public interface DeleteMenuBurger {
    void handle(Integer idBurger,Integer deletedBy);
}
