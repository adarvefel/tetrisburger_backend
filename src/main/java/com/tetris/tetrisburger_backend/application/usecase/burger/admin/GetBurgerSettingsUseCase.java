package com.tetris.tetrisburger_backend.application.usecase.burger.admin;

import com.tetris.tetrisburger_backend.domain.model.BurgerSettings;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.GetBurgerSettings;
import com.tetris.tetrisburger_backend.domain.port.out.SettingsRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class GetBurgerSettingsUseCase implements GetBurgerSettings {

    private static final Logger logger = LoggerFactory.getLogger(GetBurgerSettingsUseCase.class);
    private final SettingsRepository settingsRepository;

    public GetBurgerSettingsUseCase(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @Override
    public BurgerSettings handle() {
        logger.info(" Obteniendo configuración de hamburguesas");
        BurgerSettings settings = settingsRepository.getBurgerSettings();
        logger.info(" Configuraciones obtenidas: min=${}, max=${}, enabled={}",
                settings.getCustomBurgerMinPrice(),
                settings.getCustomBurgerMaxPrice(),
                settings.isCustomBurgersEnabled()
        );
        return settings;
    }

}
