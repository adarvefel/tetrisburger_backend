package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.model.WhatsappSettings;
import java.util.Optional;

public interface WhatsappSettingsRepository {
    Optional<WhatsappSettings> findFirst();
    WhatsappSettings save(WhatsappSettings settings);
}