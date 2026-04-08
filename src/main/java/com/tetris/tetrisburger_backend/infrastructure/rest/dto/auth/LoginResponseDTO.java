package com.tetris.tetrisburger_backend.infrastructure.rest.dto.auth;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para autenticación exitosa
 */
public record LoginResponseDTO(
        String token,
        String tokenType,
        Long expiresIn,
        UserInfoDTO user,
        LocalDateTime timestamp
) {}
