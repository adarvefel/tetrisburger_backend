package com.tetris.tetrisburger_backend.application.event;

public record ProductImageUploadRequestedEvent(
        Integer productId,
        byte[] imageBytes,
        String contentType,
        String originalFilename,
        Integer updatedBy
) {}
