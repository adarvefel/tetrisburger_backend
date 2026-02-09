package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;

public interface ImageStoragePort {

    // ====== USUARIOS ======
    ImageUploadResult uploadUserImage(byte[] bytes, String contentType, String originalFileName) throws Exception;

    // ====== PRODUCTOS ======
    ImageUploadResult uploadProductImage(FileData fileData);

    // ====== BURGERS CUSTOM ======
    ImageUploadResult uploadCustomBurgerImage(FileData fileData, Integer burgerId, Integer userId);

    // ====== BURGERS MENÚ ======
    ImageUploadResult uploadMenuBurgerImage(FileData fileData, Integer burgerId, Integer adminId);

    // ====== GENÉRICO (deprecado/legacy) ======
    @Deprecated  // Usar métodos específicos
    ImageUploadResult uploadImage(FileData fileData, String folder);

    // ====== UTILIDADES ======
    void deleteImage(String imageKey);

    String getImageUrl(String imageKey);
}
