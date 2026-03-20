package com.tetris.tetrisburger_backend.application.event;

public record MenuImageUploadRequestedEvent(
        Integer idMenu,
        byte[] fileBytes,
        String contentType,
        String originalFileName,
        Integer uploadedBy
) {}