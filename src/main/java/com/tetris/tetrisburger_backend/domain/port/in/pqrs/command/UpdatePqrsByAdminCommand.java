package com.tetris.tetrisburger_backend.domain.port.in.pqrs.command;

public record UpdatePqrsByAdminCommand(
    Integer idPqrs,
    String status,
    String priority,
    String response,
    Integer assignedTo

)
{}
