package com.tetris.tetrisburger_backend.domain.port.in.user.command;

public record UpdateUserImageByAdminCommand(
        Integer idUser,
        byte[] fileBytes,
        String contentType,
        String originalFileName,
        Integer updatedBy
) {}
