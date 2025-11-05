package com.tetris.tetrisburger_backend.infrastructure.rest.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequestDTO(
        @NotBlank(message = "El token de Google es requerido")
        String token
) {}
