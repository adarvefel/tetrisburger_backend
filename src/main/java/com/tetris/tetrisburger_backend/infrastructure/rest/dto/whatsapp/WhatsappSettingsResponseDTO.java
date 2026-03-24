package com.tetris.tetrisburger_backend.infrastructure.rest.dto.whatsapp;

import java.time.LocalDateTime;

public record WhatsappSettingsResponseDTO(
        Integer idSettings,
        String businessNumber,
        String apiKey,
        String messageTemplate,
        boolean autoSend,
        LocalDateTime updatedAt
) {}