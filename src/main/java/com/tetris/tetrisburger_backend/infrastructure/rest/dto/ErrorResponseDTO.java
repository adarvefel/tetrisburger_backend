package com.tetris.tetrisburger_backend.infrastructure.rest.dto;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        Integer status,
        String error,
        String message,
        LocalDateTime timestamp,
        String path
) {
}
