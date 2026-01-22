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

    // ====== NUEVO MÉTODO DEL PUERTO (el que usará tu listener) ======
    @Override
    public ImageUploadResult uploadUserImage(byte[] bytes, String contentType, String originalFileName) throws Exception {
        return uploadImage(bytes, contentType, originalFileName, "users/");
    }

    // ====== Helper para reusar lógica y permitir prefijos ======
    public ImageUploadResult uploadImage(byte[] bytes, String contentType, String originalFileName, String prefix) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        // Validar tipo de archivo
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen");
        }

        // Validar tamaño (máx 5MB)
        if (bytes.length > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("La imagen no puede superar 5MB");
        }

        String normalizedPrefix = normalizePrefix(prefix);

        // Obtener extensión (si hay)
        String extension = "";
        String baseName = originalFileName;

        if (originalFileName != null && originalFileName.contains(".")) {
            int lastDot = originalFileName.lastIndexOf(".");
            extension = originalFileName.substring(lastDot);
            baseName = originalFileName.substring(0, lastDot);
        }

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

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes)); // AWS SDK v2 [web:1752]

        return new ImageUploadResult(key, originalFileName);
    }

    // ====== (Opcional) helper para el controller/usecase que todavía tenga MultipartFile ======
    public ImageUploadResult uploadUserImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        return uploadImage(file.getBytes(), file.getContentType(), file.getOriginalFilename(), "users/");
    }

    @Override
    public void deleteImage(String imageKey) {
        if (imageKey == null || imageKey.isEmpty()) return;

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(imageKey)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    @Override
    public String getImageUrl(String imageKey) {
        if (imageKey == null || imageKey.isEmpty()) return null;

        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                region,
                imageKey);
    }

    private String normalizePrefix(String prefix) {
        String p = (prefix == null) ? "" : prefix.trim();
        if (p.isEmpty()) return "";

        if (p.contains("..") || p.startsWith("/") || p.contains("\\")) {
            throw new IllegalArgumentException("Prefijo inválido: " + prefix);
        }

        if (!p.endsWith("/")) p += "/";
        return p;
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null) return "file";
        return fileName
                .replaceAll("[^a-zA-Z0-9.-]", "_")
                .replaceAll("_{2,}", "_")
                .toLowerCase();
    }
}
