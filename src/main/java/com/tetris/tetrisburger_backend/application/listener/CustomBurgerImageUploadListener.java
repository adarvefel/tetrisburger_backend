package com.tetris.tetrisburger_backend.application.listener;

import com.tetris.tetrisburger_backend.application.event.CustomBurgerImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.BurgerJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
@Component
public class CustomBurgerImageUploadListener {

    private final BurgerJpaRepository burgerJpaRepository;
    private final ImageStoragePort imageStoragePort;

    private final Logger logger = LoggerFactory.getLogger(CustomBurgerImageUploadListener.class);


    public CustomBurgerImageUploadListener(BurgerJpaRepository burgerJpaRepository, ImageStoragePort imageStoragePort) {
        this.burgerJpaRepository = burgerJpaRepository;
        this.imageStoragePort = imageStoragePort;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCustomBurgerImageUpload(CustomBurgerImageUploadRequestedEvent event) {
        logger.info("Procesando imagen para custom burger: {}", event.idBurger());

        try {
            if (!burgerJpaRepository.existsByIdBurger(event.idBurger())) {
                return;
            }

            FileData fileData = new FileData(
                    event.originalFileName(),
                    event.contentType(),
                    event.fileBytes()
            );

            // ✅ LIMPIO: No conoce carpetas S3
            ImageUploadResult result = imageStoragePort.uploadCustomBurgerImage(
                    fileData,
                    event.idBurger(),
                    event.idUser()
            );

            String imageUrl = imageStoragePort.getImageUrl(result.imageKey());

            burgerJpaRepository.updateImageFields(
                    event.idBurger(),
                    imageUrl,
                    result.imageKey(),
                    event.idUser()
            );

            logger.info("✅ Imagen actualizada para burger {}", event.idBurger());

        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage(), e);
        }
    }
}