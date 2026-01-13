package com.tetris.tetrisburger_backend.domain.port.out;

public record ImageUploadResult(
        String imageKey,
        String originalFileName
) {}
