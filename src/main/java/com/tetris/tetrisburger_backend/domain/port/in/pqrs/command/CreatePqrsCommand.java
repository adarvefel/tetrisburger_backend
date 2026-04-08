package com.tetris.tetrisburger_backend.domain.port.in.pqrs.command;

public record CreatePqrsCommand(
        String type,
        String subject,
        String description,
        Integer idUser
)

{}
