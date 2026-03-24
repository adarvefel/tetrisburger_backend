package com.tetris.tetrisburger_backend.application.usecase.burger.admin;

import com.tetris.tetrisburger_backend.domain.model.BurgerSettings;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.UpdateBurgerSettings;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateBurgerSettingsCommand;
import com.tetris.tetrisburger_backend.domain.port.out.SettingsRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UpdateBurgerSettingsUseCase implements UpdateBurgerSettings {

    private final SettingsRepository settingsRepository;

    public UpdateBurgerSettingsUseCase(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    public BurgerSettings handle(UpdateBurgerSettingsCommand command) {
        BurgerSettings settings = settingsRepository.getBurgerSettings();

        settings.update(
                command.customBurgerMinPrice(),
                command.customBurgerMaxPrice(),
                command.minIngredients(),
                command.maxIngredients(),
                command.customBurgersEnabled()
        );

        return settingsRepository.save(settings);
    }
}
