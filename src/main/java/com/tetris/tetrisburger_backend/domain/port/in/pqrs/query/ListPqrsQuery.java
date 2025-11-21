package com.tetris.tetrisburger_backend.domain.port.in.pqrs.query;

public record ListPqrsQuery(
        int page,
        int size,
        String sortBy,
        String type,
        String status,
        String priority
) {}
