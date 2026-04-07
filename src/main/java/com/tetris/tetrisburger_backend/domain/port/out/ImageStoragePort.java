package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;

public interface ImageStoragePort {

    // ====== USUARIOS ======
    ImageUploadResult uploadUserImage(byte[] bytes, String contentType, String originalFileName);

    // ====== PRODUCTOS ======
    ImageUploadResult uploadProductImage(FileData fileData);

    
    // ====== BURGERS MENÚ ======
    ImageUploadResult uploadMenuBurgerImage(FileData fileData, Integer burgerId, Integer adminId);

    // ====== GENÉRICO (deprecado/legacy) ======
    @Deprecated  // Usar métodos específicos
    ImageUploadResult uploadImage(FileData fileData, String folder);

    ImageUploadResult uploadAdditionImage(FileData fileData);


    ImageUploadResult uploadMenuImage(FileData fileData);


    // ====== UTILIDADES ======
    void deleteImage(String imageKey);

    String getImageUrl(String imageKey);

    // ====== FACTURAS ======
    ImageUploadResult uploadInvoicePdf(byte[] bytes);
}
