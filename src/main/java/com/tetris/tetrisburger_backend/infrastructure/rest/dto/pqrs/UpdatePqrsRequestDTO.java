package com.tetris.tetrisburger_backend.infrastructure.rest.dto.pqrs;

public record UpdatePqrsRequestDTO(
        String type,
        String subject,
        String description
)
{}
