package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.model.AdditionSettings;

public interface AdditionSettingsRepository {
    AdditionSettings get();
    AdditionSettings save(AdditionSettings settings);
}