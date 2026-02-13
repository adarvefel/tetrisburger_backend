package com.tetris.tetrisburger_backend.application.usecase.burger.admin;

import com.tetris.tetrisburger_backend.domain.model.BurgerSettings;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.UpdateBurgerSettings;

import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateBurgerSettingsCommand;
import com.tetris.tetrisburger_backend.domain.port.out.SettingsRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UpdateBurgerSettingsUseCase implements UpdateBurgerSettings {

    private static final Logger logger = LoggerFactory.getLogger(UpdateBurgerSettingsUseCase.class);
    private final SettingsRepository settingsRepository;

    public UpdateBurgerSettingsUseCase(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    public BurgerSettings handle(UpdateBurgerSettingsCommand command) {
        BurgerSettings settings = settingsRepository.getBurgerSettings();

        logger.info(" Valores anteriores: min=${}, max=${}, enabled={}",
                settings.getCustomBurgerMinPrice(),
                settings.getCustomBurgerMaxPrice(),
                settings.isCustomBurgersEnabled()
        );

        settings.update(
                command.customBurgerMinPrice(),
                command.customBurgerMaxPrice(),
                command.minIngredients(),
                command.maxIngredients(),
                command.customBurgersEnabled()
        );

        BurgerSettings updated = settingsRepository.save(settings);

        logger.info(" Settings actualizadas: min=${}, max=${}, enabled={}",
                updated.getCustomBurgerMinPrice(),
                updated.getCustomBurgerMaxPrice(),
                updated.isCustomBurgersEnabled()
        );

        return updated;
    }
}
