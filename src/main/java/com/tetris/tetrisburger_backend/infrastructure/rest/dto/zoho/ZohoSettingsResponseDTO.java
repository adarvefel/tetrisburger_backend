package com.tetris.tetrisburger_backend.infrastructure.rest.dto.zoho;

import java.time.LocalDateTime;

public record ZohoSettingsResponseDTO(
        String organizationId,
        LocalDateTime updatedAt
) {}
