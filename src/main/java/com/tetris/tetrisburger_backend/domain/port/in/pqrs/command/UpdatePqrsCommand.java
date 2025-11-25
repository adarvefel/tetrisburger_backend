package com.tetris.tetrisburger_backend.domain.port.in.pqrs.command;

public record UpdatePqrsCommand(
        Integer idPqrs,
        String type,
        String subject,
        String description,
        Integer idUser
)

{}
