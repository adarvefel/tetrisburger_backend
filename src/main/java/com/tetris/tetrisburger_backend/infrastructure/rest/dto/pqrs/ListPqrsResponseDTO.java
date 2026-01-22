package com.tetris.tetrisburger_backend.infrastructure.rest.dto.pqrs;
import java.util.List;

public record ListPqrsResponseDTO(
        List<PqrsResponseDTO> pqrs,
        int page,
        int size,
        long totalElements,
        int totalPages
)
{}
