package com.tetris.tetrisburger_backend.domain.port.in.menu;

import com.tetris.tetrisburger_backend.domain.model.Menu;

public interface DeleteMenu {
    Menu handle(Integer id, Integer deletedBy);
}