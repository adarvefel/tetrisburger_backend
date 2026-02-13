package com.tetris.tetrisburger_backend.domain.port.in.pqrs.command;

public record GetPqrsByIdCommand(
        Integer idPqrs,
        Integer idUser
) {
}
