package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.model.BurgerSettings;
import com.tetris.tetrisburger_backend.domain.port.out.SettingsRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.BurgerSettingsEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.BurgerSettingsEnitityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.BurgerSettingsJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SettingsRepositoryAdapter implements SettingsRepository {

    private static final Logger logger = LoggerFactory.getLogger(SettingsRepositoryAdapter.class);
    private static final Integer SETTINGS_ID = 1; // ID único fijo

    private final BurgerSettingsJpaRepository jpaRepository;
    private final BurgerSettingsEnitityMapper mapper;

    public SettingsRepositoryAdapter(
            BurgerSettingsJpaRepository jpaRepository,
            BurgerSettingsEnitityMapper mapper
    ) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public BurgerSettings getBurgerSettings() {
        logger.debug("📋 Buscando settings con ID: {}", SETTINGS_ID);

        return jpaRepository.findById(SETTINGS_ID)
                .map(entity -> {
                    logger.debug("✅ Settings encontradas en BD");
                    return mapper.toDomain(entity);
                })
                .orElseGet(() -> {
                    logger.warn("⚠️ Settings no encontradas, creando defaults");
                    return createDefaultSettings();
                });
    }

    @Override
    public BurgerSettings save(BurgerSettings settings) {
        logger.debug("💾 Guardando settings: min={}, max={}, enabled={}",
                settings.getCustomBurgerMinPrice(),
                settings.getCustomBurgerMaxPrice(),
                settings.isCustomBurgersEnabled()
        );

        BurgerSettingsEntity entity = mapper.toEntity(settings);
        BurgerSettingsEntity saved = jpaRepository.save(entity);

        logger.info(" Settings guardadas exitosamente");
        return mapper.toDomain(saved);
    }

    private BurgerSettings createDefaultSettings() {
        BurgerSettings defaults = BurgerSettings.createDefaults();
        BurgerSettingsEntity entity = mapper.toEntity(defaults);
        BurgerSettingsEntity saved = jpaRepository.save(entity);

        logger.info(" Settings por defecto creadas y guardadas");
        return mapper.toDomain(saved);
    }
}
