package com.tetris.tetrisburger_backend.infrastructure.rest.dto;

public record MessageResponseDTO(
        String message,
        boolean success,
        long timestamp
) {
    public MessageResponseDTO(String message, boolean success) {
        this(message, success, System.currentTimeMillis());
    }
}
