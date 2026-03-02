package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Component
public class S3ImageStorageAdapter implements ImageStoragePort {

    private static final String USERS_FOLDER = "users";
    private static final String PRODUCTS_FOLDER = "products";
    private static final String MENU_BURGERS_FOLDER = "burgers";

    @Value("${s3.folder.addition:addition}")
    private String additionFolder;

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

    // ====== USUARIOS ======
    @Override
    public ImageUploadResult uploadUserImage(byte[] bytes, String contentType, String originalFileName) {
        return uploadImageInternal(bytes, contentType, originalFileName, USERS_FOLDER);
    }

    // ====== PRODUCTOS ======
    @Override
    public ImageUploadResult uploadProductImage(FileData fileData) {
        return uploadImageInternal(
                fileData.bytes(),
                fileData.contentType(),
                fileData.originalFilename(),
                PRODUCTS_FOLDER
        );
    }

    // ====== BURGERS MENÚ ======
    @Override
    public ImageUploadResult uploadMenuBurgerImage(FileData fileData, Integer burgerId, Integer adminId) {
        return uploadImageInternal(
                fileData.bytes(),
                fileData.contentType(),
                fileData.originalFilename(),
                MENU_BURGERS_FOLDER
        );
    }

    @Override
    @Deprecated
    public ImageUploadResult uploadImage(FileData fileData, String folder) {
        if (fileData == null || fileData.bytes() == null || fileData.bytes().length == 0) {
            return null;
        }
        return uploadImageInternal(
                fileData.bytes(),
                fileData.contentType(),
                fileData.originalFilename(),
                folder
        );
    }

    @Override
    public ImageUploadResult uploadAdditionImage(FileData fileData) {
        return uploadImageInternal(
                fileData.bytes(),
                fileData.contentType(),
                fileData.originalFilename(),
                additionFolder
        );
    }

    private ImageUploadResult uploadImageInternal(
            byte[] bytes,
            String contentType,
            String originalFileName,
            String folder
    ) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException(
                    "El archivo debe ser una imagen. ContentType: " + contentType
            );
        }

        if (bytes.length > 5 * 1024 * 1024) {
            throw new IllegalArgumentException(
                    String.format("La imagen no puede superar 5MB. Tamaño: %.2f MB",
                            bytes.length / (1024.0 * 1024.0))
            );
        }

        String normalizedFolder = normalizeFolder(folder);

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
                normalizedFolder,
                System.currentTimeMillis(),
                uniqueId,
                sanitizedName,
                extension);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

            return new ImageUploadResult(key, originalFileName);

        } catch (Exception e) {
            throw new RuntimeException("Error al subir imagen a S3: " + e.getMessage(), e);
        }
    }

    // ====== DELETE IMAGE ======
    @Override
    public void deleteImage(String imageKey) {
        if (imageKey == null || imageKey.isEmpty()) {
            return;
        }

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(imageKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

        } catch (Exception e) {
            // El fallo al eliminar la imagen no debe interrumpir el flujo principal
        }
    }

    // ====== GET IMAGE URL ======
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

    // ====== HELPERS ======
    private String normalizeFolder(String folder) {
        String f = (folder == null) ? "" : folder.trim();
        if (f.isEmpty()) return "";

        if (f.contains("..") || f.startsWith("/") || f.contains("\\")) {
            throw new IllegalArgumentException("Folder inválido: " + folder);
        }

        if (!f.endsWith("/")) {
            f += "/";
        }

        return f;
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "file";
        }

        return fileName
                .replaceAll("[^a-zA-Z0-9.-]", "_")
                .replaceAll("_{2,}", "_")
                .toLowerCase();
    }
}
