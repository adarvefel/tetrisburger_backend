package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.common.FileData;

public interface ImageStoragePort {
    ImageUploadResult uploadUserImage(byte[] bytes, String contentType, String originalFileName) throws Exception;


    // Métodos para productos
    ImageUploadResult uploadProductImage(FileData fileData);


    void deleteImage(String imageKey);
    String getImageUrl(String imageKey);
}

