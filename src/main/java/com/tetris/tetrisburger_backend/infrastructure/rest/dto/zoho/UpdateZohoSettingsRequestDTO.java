package com.tetris.tetrisburger_backend.infrastructure.rest.dto.zoho;

import jakarta.validation.constraints.NotBlank;

public record UpdateZohoSettingsRequestDTO(
        @NotBlank String clientId,
        @NotBlank String clientSecret,
        @NotBlank String accessToken,
        @NotBlank String refreshToken,
        @NotBlank String organizationId
) {}
