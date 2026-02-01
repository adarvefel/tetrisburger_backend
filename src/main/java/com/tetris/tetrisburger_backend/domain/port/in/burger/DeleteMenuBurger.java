package com.tetris.tetrisburger_backend.domain.port.in.burger;

public interface DeleteMenuBurger {
    void handle(Integer idBurger,Integer deletedBy);
}
