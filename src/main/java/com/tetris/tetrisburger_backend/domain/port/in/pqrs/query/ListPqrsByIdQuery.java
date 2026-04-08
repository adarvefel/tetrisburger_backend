package com.tetris.tetrisburger_backend.domain.port.in.pqrs.query;

public record ListPqrsByIdQuery(
        int page,
        int size,
        String storBy
)
{}
