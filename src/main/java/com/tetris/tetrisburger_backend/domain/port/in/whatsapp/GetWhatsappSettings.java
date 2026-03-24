package com.tetris.tetrisburger_backend.domain.port.in.whatsapp;

import com.tetris.tetrisburger_backend.domain.model.WhatsappSettings;

public interface GetWhatsappSettings {
    WhatsappSettings handle();
}