package com.tetris.tetrisburger_backend.domain.port.in.menu;

import com.tetris.tetrisburger_backend.domain.model.Menu;
import com.tetris.tetrisburger_backend.domain.port.in.menu.command.UpdateMenuCommand;

public interface UpdateMenu {
    Menu handle(UpdateMenuCommand command);
}
