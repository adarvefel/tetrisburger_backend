package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.model.BurgerSettings;

public interface SettingsRepository {

    BurgerSettings getBurgerSettings();

    BurgerSettings save(BurgerSettings settings);
}
