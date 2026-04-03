package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.model.BurgerSettings;
import com.tetris.tetrisburger_backend.domain.port.out.SettingsRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.BurgerSettingsEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.BurgerSettingsEnitityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.BurgerSettingsJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public class SettingsRepositoryAdapter implements SettingsRepository {

    private static final Integer SETTINGS_ID = 1;

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
        
        return jpaRepository.findById(SETTINGS_ID)
                .map(entity -> {
                    return mapper.toDomain(entity);
                })
                .orElseGet(() -> {
                    return createDefaultSettings();
                });
    }

    @Override
    public BurgerSettings save(BurgerSettings settings) {


        BurgerSettingsEntity entity = mapper.toEntity(settings);
        BurgerSettingsEntity saved = jpaRepository.save(entity);

        return mapper.toDomain(saved);
    }

    private BurgerSettings createDefaultSettings() {
        BurgerSettings defaults = BurgerSettings.createDefaults();
        BurgerSettingsEntity entity = mapper.toEntity(defaults);
        BurgerSettingsEntity saved = jpaRepository.save(entity);

                return mapper.toDomain(saved);
    }
}
