package com.tetris.tetrisburger_backend.infrastructure.rest.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponseDTO(
        Integer status,
        String error,
        String message,
        LocalDateTime timestamp,
        String path,
        Map<String, String> errors
) {
}
