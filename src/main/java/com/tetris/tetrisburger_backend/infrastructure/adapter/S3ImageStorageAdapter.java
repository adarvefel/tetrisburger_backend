package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;
@Component
public class S3ImageStorageAdapter implements ImageStoragePort {

    private static final Logger logger = LoggerFactory.getLogger(S3ImageStorageAdapter.class);

    // ====== CONSTANTES DE CARPETAS ======
    private static final String USERS_FOLDER = "users";
    private static final String PRODUCTS_FOLDER = "products";
    private static final String MENU_BURGERS_FOLDER = "burgers-menu";

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
        logger.info("S3ImageStorageAdapter initialized - Bucket: {}, Region: {}", bucketName, region);
    }

    // ====== USUARIOS ======
    @Override
    public ImageUploadResult uploadUserImage(byte[] bytes, String contentType, String originalFileName)  {
        logger.info("Uploading user image: {}", originalFileName);
        return uploadImageInternal(bytes, contentType, originalFileName, USERS_FOLDER);
    }

    // ====== PRODUCTOS ======
    @Override
    public ImageUploadResult uploadProductImage(FileData fileData) {
        logger.info("Uploading product image: {}", fileData.originalFilename());
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
        logger.info("Uploading menu burger image: burgerId={}, adminId={}", burgerId, adminId);

        return uploadImageInternal(
                fileData.bytes(),
                fileData.contentType(),
                fileData.originalFilename(),
                MENU_BURGERS_FOLDER
        );
    }

    // ====== GENÉRICO (deprecado) ======
    @Override
    @Deprecated
    public ImageUploadResult uploadImage(FileData fileData, String folder) {
        if (fileData == null || fileData.bytes() == null || fileData.bytes().length == 0) {
            logger.warn("FileData is null or empty, skipping upload");
            return null;
        }

        logger.warn("Using deprecated uploadImage() method with folder: {}", folder);

        return uploadImageInternal(
                fileData.bytes(),
                fileData.contentType(),
                fileData.originalFilename(),
                folder
        );
    }

    // ====== MÉTODO INTERNO REUTILIZABLE (PRIVATE) ======
    private ImageUploadResult uploadImageInternal(
            byte[] bytes,
            String contentType,
            String originalFileName,
            String folder
    ) {
        if (bytes == null || bytes.length == 0) {
            logger.warn("Bytes are null or empty, skipping upload");
            return null;
        }

        // Validar tipo de archivo
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException(
                    "El archivo debe ser una imagen. ContentType: " + contentType
            );
        }

        // Validar tamaño (máx 5MB)
        if (bytes.length > 5 * 1024 * 1024) {
            throw new IllegalArgumentException(
                    String.format("La imagen no puede superar 5MB. Tamaño: %.2f MB",
                            bytes.length / (1024.0 * 1024.0))
            );
        }

        String normalizedFolder = normalizeFolder(folder);

        // Obtener extensión
        String extension = "";
        String baseName = originalFileName;

        if (originalFileName != null && originalFileName.contains(".")) {
            int lastDot = originalFileName.lastIndexOf(".");
            extension = originalFileName.substring(lastDot);
            baseName = originalFileName.substring(0, lastDot);
        }

        String sanitizedName = sanitizeFileName(baseName);
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);

        // Formato: folder/timestamp-uuid-filename.ext
        String key = String.format("%s%d-%s-%s%s",
                normalizedFolder,
                System.currentTimeMillis(),
                uniqueId,
                sanitizedName,
                extension);

        logger.info("Generated S3 key: {}", key);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

            logger.info("Image uploaded successfully. Bucket: {}, Key: {}", bucketName, key);

            return new ImageUploadResult(key, originalFileName);

        } catch (Exception e) {
            logger.error("Error uploading to S3. Bucket: {}, Key: {}", bucketName, key, e);
            throw new RuntimeException("Error al subir imagen a S3: " + e.getMessage(), e);
        }
    }

    // ====== DELETE IMAGE ======
    @Override
    public void deleteImage(String imageKey) {
        if (imageKey == null || imageKey.isEmpty()) {
            logger.warn("ImageKey is null or empty, skipping deletion");
            return;
        }

        logger.info("Deleting image: {}", imageKey);

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(imageKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            logger.info("Image deleted successfully: {}", imageKey);

        } catch (Exception e) {
            logger.error("Error deleting image: {}", imageKey, e);
        }
    }

    // ====== GET IMAGE URL ======
    @Override
    public String getImageUrl(String imageKey) {
        if (imageKey == null || imageKey.isEmpty()) {
            return null;
        }

        String url = String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                region,
                imageKey);

        logger.debug("Generated image URL: {}", url);
        return url;
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
