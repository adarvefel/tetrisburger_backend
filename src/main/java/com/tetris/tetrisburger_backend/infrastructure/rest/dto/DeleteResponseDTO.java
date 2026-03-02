package com.tetris.tetrisburger_backend.infrastructure.rest.dto;

public record DeleteResponseDTO(
        String message,
        boolean success,
        DeletedResourceDTO data
) {
    public record DeletedResourceDTO(
            Integer id,
            String name) {}
}
