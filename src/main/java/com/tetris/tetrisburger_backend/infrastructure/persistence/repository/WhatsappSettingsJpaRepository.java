package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.WhatsappSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WhatsappSettingsJpaRepository
        extends JpaRepository<WhatsappSettingsEntity, Integer> {
}