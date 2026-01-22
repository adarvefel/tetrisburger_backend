package com.tetris.tetrisburger_backend.domain.common;

public record FileData(
        String originalFilename,
        String contentType,
        byte[] bytes
) {
}
