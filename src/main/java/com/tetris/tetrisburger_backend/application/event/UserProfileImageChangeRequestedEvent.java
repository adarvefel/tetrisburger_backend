package com.tetris.tetrisburger_backend.application.event;

public record UserProfileImageChangeRequestedEvent(
        Integer idUser,
        byte[] fileBytes,
        String contentType,
        String originalFileName,
        String oldImageKey
) {}
