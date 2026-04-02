package com.tetris.tetrisburger_backend.infrastructure.rest.dto.whatsapp;

public record UpdateWhatsappSettingsRequestDTO(
        String businessNumber,
        String apiKey,
        String messageTemplate,
        boolean autoSend
) {}