package com.tetris.tetrisburger_backend.domain.port.in.menu;

import com.tetris.tetrisburger_backend.domain.model.Menu;
import com.tetris.tetrisburger_backend.domain.port.in.menu.command.UpdateMenuImageCommand;

public interface UpdateMenuImage {
    Menu handle(UpdateMenuImageCommand command);
}