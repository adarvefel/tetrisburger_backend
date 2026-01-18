package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.domain.port.out.ImageUploadResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Component
public class S3ImageStorageAdapter implements ImageStoragePort {

    private final S3Client s3Client;
    private final String bucketName;
    private final String region;

    public S3ImageStorageAdapter(
            S3Client s3Client,
            @Value("${aws.s3.bucket}") String bucketName,
            @Value("${aws.region}") String region
    ) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.region = region;
    }

    /**
     * Compatibilidad: el método del puerto sigue existiendo y por defecto guarda en users/.
     */
    @Override
    public ImageUploadResult uploadUserImage(MultipartFile file) throws IOException {
        return uploadImage(file, "users/");
    }

    /**
     * Abierto: permite guardar bajo cualquier "carpeta" (prefijo) como users/, products/, menus/, etc.
     * En S3 las "carpetas" son prefijos del object key.
     */
    public ImageUploadResult uploadImage(MultipartFile file, String prefix) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Validar tipo de archivo
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen");
        }

        // Validar tamaño (máx 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("La imagen no puede superar 5MB");
        }

        // Normalizar prefijo ("products" -> "products/") y bloquear prefijos inválidos
        String normalizedPrefix = normalizePrefix(prefix);

        // Obtener nombre original y extensión
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        String baseName = originalFilename;

        if (originalFilename != null && originalFilename.contains(".")) {
            int lastDot = originalFilename.lastIndexOf(".");
            extension = originalFilename.substring(lastDot);
            baseName = originalFilename.substring(0, lastDot);
        }

        // Generar key único: <prefix>timestamp-uuid-nombre-sanitizado.ext
        String sanitizedName = sanitizeFileName(baseName);
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);

        String key = String.format("%s%d-%s-%s%s",
                normalizedPrefix,
                System.currentTimeMillis(),
                uniqueId,
                sanitizedName,
                extension);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        return new ImageUploadResult(key, originalFilename);
    }

    @Override
    public void deleteImage(String imageKey) {
        if (imageKey == null || imageKey.isEmpty()) {
            return;
        }

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(imageKey)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    // (Opcional) si lo tienes usado en otras partes, lo dejamos como alias:
    public void delete(String key) {
        deleteImage(key);
    }

    @Override
    public String getImageUrl(String imageKey) {
        if (imageKey == null || imageKey.isEmpty()) {
            return null;
        }
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                region,
                imageKey);
    }

    /**
     * Normaliza y valida el prefijo.
     * - null/blank => "" (sin carpeta)
     * - "products" => "products/"
     * - Bloquea traversal y prefijos raros
     */
    private String normalizePrefix(String prefix) {
        String p = (prefix == null) ? "" : prefix.trim();

        if (p.isEmpty()) return "";

        // Seguridad básica: evitar cosas como "../", rutas absolutas o backslashes
        if (p.contains("..") || p.startsWith("/") || p.contains("\\")) {
            throw new IllegalArgumentException("Prefijo inválido: " + prefix);
        }

        if (!p.endsWith("/")) p += "/";
        return p;
    }

    // Limpiar caracteres especiales del nombre
    private String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "file";
        }
        return fileName
                .replaceAll("[^a-zA-Z0-9.-]", "_")
                .replaceAll("_{2,}", "_")
                .toLowerCase();
    }
}
