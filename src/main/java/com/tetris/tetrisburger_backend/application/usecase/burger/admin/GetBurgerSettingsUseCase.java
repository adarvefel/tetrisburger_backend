package com.tetris.tetrisburger_backend.application.usecase.burger.admin;

import com.tetris.tetrisburger_backend.domain.model.BurgerSettings;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.GetBurgerSettings;
import com.tetris.tetrisburger_backend.domain.port.out.SettingsRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class GetBurgerSettingsUseCase implements GetBurgerSettings {

    private final SettingsRepository settingsRepository;

    public GetBurgerSettingsUseCase(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    public BurgerSettings handle() {
        return settingsRepository.getBurgerSettings();
    }
}
