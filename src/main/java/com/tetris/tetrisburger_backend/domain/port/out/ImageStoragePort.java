package com.tetris.tetrisburger_backend.domain.port.out;

public interface ImageStoragePort {
    ImageUploadResult uploadUserImage(byte[] bytes, String contentType, String originalFileName) throws Exception;
    void deleteImage(String imageKey);
    String getImageUrl(String imageKey);
}

