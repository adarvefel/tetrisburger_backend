package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;

public interface ImageStoragePort {
    ImageUploadResult uploadUserImage(byte[] bytes, String contentType, String originalFileName) throws Exception;


    // Métodos para productos
    ImageUploadResult uploadProductImage(FileData fileData);

    ImageUploadResult uploadImage(FileData fileData, String folder);


    void deleteImage(String imageKey);
    String getImageUrl(String imageKey);
}

