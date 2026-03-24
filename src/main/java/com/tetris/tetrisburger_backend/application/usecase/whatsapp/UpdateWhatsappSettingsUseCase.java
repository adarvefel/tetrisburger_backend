package com.tetris.tetrisburger_backend.application.usecase.whatsapp;

import com.tetris.tetrisburger_backend.domain.model.WhatsappSettings;
import com.tetris.tetrisburger_backend.domain.port.in.whatsapp.UpdateWhatsappSettings;
import com.tetris.tetrisburger_backend.domain.port.out.WhatsappSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateWhatsappSettingsUseCase implements UpdateWhatsappSettings {

    private final WhatsappSettingsRepository repository;

    public UpdateWhatsappSettingsUseCase(WhatsappSettingsRepository repository) {
        this.repository = repository;
    }

    @Override
    public WhatsappSettings handle(String businessNumber, String apiKey, String messageTemplate, boolean autoSend) {
        WhatsappSettings settings = repository.findFirst()
                .orElseThrow(() -> new RuntimeException("No hay configuración de WhatsApp"));
        settings.update(businessNumber, apiKey, messageTemplate, autoSend);
        return repository.save(settings);
    }
}