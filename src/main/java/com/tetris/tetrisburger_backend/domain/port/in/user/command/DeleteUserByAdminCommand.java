package com.tetris.tetrisburger_backend.domain.port.in.user.command;

public record DeleteUserByAdminCommand(
        Integer idUser,
        Integer deletedBy  // ID del admin que elimina
) {}
