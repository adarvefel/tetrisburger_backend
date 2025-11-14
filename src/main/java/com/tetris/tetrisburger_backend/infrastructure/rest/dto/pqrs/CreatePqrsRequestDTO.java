package com.tetris.tetrisburger_backend.infrastructure.rest.dto.pqrs;

public record CreatePqrsRequestDTO(
        String type,
        String subject,
        String description
)
{}
