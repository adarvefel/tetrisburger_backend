package com.tetris.tetrisburger_backend.domain.common;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public record FileData(
        String originalFilename,
        String contentType,
        byte[] bytes

) {
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
            throw new IllegalArgumentException("Error al leer archivo: " + e.getMessage(), e);
        }
    }


    public boolean isValid() {
        return bytes != null && bytes.length > 0 && originalFilename != null;
    }
}
