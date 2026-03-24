package com.tetris.tetrisburger_backend.application.usecase.whatsapp;

import com.tetris.tetrisburger_backend.domain.model.WhatsappSettings;
import com.tetris.tetrisburger_backend.domain.port.in.whatsapp.GetWhatsappSettings;
import com.tetris.tetrisburger_backend.domain.port.out.WhatsappSettingsRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class GetWhatsappSettingsUseCase implements GetWhatsappSettings {

    private final WhatsappSettingsRepository repository;

    public GetWhatsappSettingsUseCase(WhatsappSettingsRepository repository) {
        this.repository = repository;
    }

    @Override
    public WhatsappSettings handle() {
        return repository.findFirst()
                .orElseThrow(() -> new RuntimeException("No hay configuración de WhatsApp"));
    }
}