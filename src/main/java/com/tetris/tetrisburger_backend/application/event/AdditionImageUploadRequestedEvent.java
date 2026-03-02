package com.tetris.tetrisburger_backend.application.event;

public record AdditionImageUploadRequestedEvent(
        Integer additionId,
        byte[] imageBytes,
        String contentType,
        String originalFilename
) {}
