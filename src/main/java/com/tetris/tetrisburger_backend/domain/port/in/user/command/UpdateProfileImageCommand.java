package com.tetris.tetrisburger_backend.domain.port.in.user.command;

public record UpdateProfileImageCommand(
        Integer idUser,
        byte[] fileBytes,
        String contentType,
        String originalFileName,
        Integer updatedBy
) {}