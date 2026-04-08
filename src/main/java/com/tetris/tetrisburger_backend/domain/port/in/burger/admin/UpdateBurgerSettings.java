package com.tetris.tetrisburger_backend.domain.port.in.burger.admin;

import com.tetris.tetrisburger_backend.domain.model.BurgerSettings;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateBurgerSettingsCommand;

public interface UpdateBurgerSettings {
    BurgerSettings handle(UpdateBurgerSettingsCommand command);
}
