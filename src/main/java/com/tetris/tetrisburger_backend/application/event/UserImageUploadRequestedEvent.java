package com.tetris.tetrisburger_backend.application.event;

public record UserImageUploadRequestedEvent(
        Integer idUser,
        byte[] fileBytes,
        String contentType,
        String originalFileName,
        Integer performedBy
) {}
