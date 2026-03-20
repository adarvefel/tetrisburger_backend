package com.tetris.tetrisburger_backend.infrastructure.rest.dto.pqrs;

public record UpdatePqrsByAdminRequestDTO(
    String status,
    String priority,
    String response

)
{}
