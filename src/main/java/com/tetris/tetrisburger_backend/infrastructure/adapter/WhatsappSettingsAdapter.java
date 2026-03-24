package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.model.WhatsappSettings;
import com.tetris.tetrisburger_backend.domain.port.out.WhatsappSettingsRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.WhatsappSettingsEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.WhatsappSettingsJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class WhatsappSettingsAdapter implements WhatsappSettingsRepository {

    private final WhatsappSettingsJpaRepository jpa;

    public WhatsappSettingsAdapter(WhatsappSettingsJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<WhatsappSettings> findFirst() {
        return jpa.findAll().stream().findFirst().map(this::toDomain);
    }

    @Override
    public WhatsappSettings save(WhatsappSettings settings) {
        WhatsappSettingsEntity entity = toEntity(settings);
        return toDomain(jpa.save(entity));
    }

    private WhatsappSettings toDomain(WhatsappSettingsEntity e) {
        return WhatsappSettings.reconstitute(
                e.getIdSettings(), e.getBusinessNumber(), e.getApiKey(),
                e.getMessageTemplate(), e.isAutoSend(), e.getUpdatedAt()
        );
    }

    private WhatsappSettingsEntity toEntity(WhatsappSettings s) {
        WhatsappSettingsEntity e = new WhatsappSettingsEntity();
        e.setIdSettings(s.getIdSettings());
        e.setBusinessNumber(s.getBusinessNumber());
        e.setApiKey(s.getApiKey());
        e.setMessageTemplate(s.getMessageTemplate());
        e.setAutoSend(s.isAutoSend());
        e.setUpdatedAt(s.getUpdatedAt());
        return e;
    }
}