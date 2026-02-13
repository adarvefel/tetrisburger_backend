package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.BurgerSettings;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.BurgerSettingsEntity;
import org.springframework.stereotype.Component;

@Component
public class BurgerSettingsEnitityMapper {

    public BurgerSettings toDomain(BurgerSettingsEntity entity) {
        if (entity == null) return null;

        BurgerSettings settings = BurgerSettings.createDefaults();
        settings.setIdSettings(entity.getIdSettings());

        settings.update(
                entity.getCustomBurgerMinPrice(),
                entity.getCustomBurgerMaxPrice(),
                entity.getMinIngredients(),
                entity.getMaxIngredients(),
                entity.getCustomBurgersEnabled()
        );

        return settings;
    }

    public BurgerSettingsEntity toEntity(BurgerSettings domain) {
        if (domain == null) return null;

        BurgerSettingsEntity entity = new BurgerSettingsEntity();
        entity.setIdSettings(domain.getIdSettings() != null ? domain.getIdSettings() : 1);
        entity.setCustomBurgerMinPrice(domain.getCustomBurgerMinPrice());
        entity.setCustomBurgerMaxPrice(domain.getCustomBurgerMaxPrice());
        entity.setMinIngredients(domain.getMinIngredients());
        entity.setMaxIngredients(domain.getMaxIngredients());
        entity.setCustomBurgersEnabled(domain.isCustomBurgersEnabled());
        entity.setUpdatedAt(domain.getUpdatedAt());

        return entity;
    }
}
