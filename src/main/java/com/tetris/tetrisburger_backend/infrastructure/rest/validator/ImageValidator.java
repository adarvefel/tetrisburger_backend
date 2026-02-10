package com.tetris.tetrisburger_backend.infrastructure.rest.validator;

import com.tetris.tetrisburger_backend.domain.exception.ImageUploadException;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Component
public class ImageValidator {

    private static final Tika tika = new Tika();
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ImageUploadException("La imagen es requerida");
        }

        validateSize(file);
        validateRealType(file);
    }

    private void validateSize(MultipartFile file) {
        if (file.getSize() > MAX_SIZE) {
            throw new ImageUploadException(
                    String.format("Imagen muy grande: %.2f MB. Máximo 5MB",
                            file.getSize() / 1024.0 / 1024.0)
            );
        }
    }

    private void validateRealType(MultipartFile file) {
        try {
            String detectedType = tika.detect(file.getInputStream());

            String filename = file.getOriginalFilename();
            if (filename != null && filename.toLowerCase().endsWith(".jfif")) {
                throw new ImageUploadException(
                        "Archivos .jfif no permitidos. Convierta a JPG/PNG primero"
                );
            }

            if (!ALLOWED_MIME_TYPES.contains(detectedType)) {
                throw new ImageUploadException(
                        String.format("Tipo de archivo no permitido. Detectado: %s. Use: JPG, PNG o WebP",
                                detectedType)
                );
            }

        } catch (IOException e) {
            throw new ImageUploadException("Error al validar archivo: " + e.getMessage());
        }
    }
}
