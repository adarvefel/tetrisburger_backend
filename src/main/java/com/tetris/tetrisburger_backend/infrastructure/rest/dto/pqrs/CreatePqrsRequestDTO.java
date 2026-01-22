package com.tetris.tetrisburger_backend.infrastructure.rest.dto.pqrs;

import jakarta.validation.constraints.NotBlank;

public record CreatePqrsRequestDTO(
        @NotBlank String type,
        @NotBlank String subject,
        @NotBlank String description
)
{}
