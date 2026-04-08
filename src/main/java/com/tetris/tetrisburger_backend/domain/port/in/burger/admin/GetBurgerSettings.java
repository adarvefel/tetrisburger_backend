package com.tetris.tetrisburger_backend.domain.port.in.burger.admin;

import com.tetris.tetrisburger_backend.domain.model.BurgerSettings;

public interface GetBurgerSettings {
    BurgerSettings handle();
}
