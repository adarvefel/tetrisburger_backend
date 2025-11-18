package com.tetris.tetrisburger_backend.domain.port.in.pqrs.command;

public record DeleteSoftPqrsCommand(
    Integer idPqrs,
    Integer idUser
)
{}
