package com.tetris.tetrisburger_backend.domain.common;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public record FileData(
        String originalFilename,
        String contentType,
        byte[] bytes
) {
    /**
     * Convierte MultipartFile a FileData.
     * Punto de entrada desde infraestructura a dominio.
     */
    public static FileData from(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            return new FileData(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo", e);
        }
    }
}
