package com.tetris.tetrisburger_backend.application.listener;

import com.tetris.tetrisburger_backend.application.event.MenuImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.MenuJpaRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class MenuImageUploadListener {

    private static final Logger logger = LoggerFactory.getLogger(MenuImageUploadListener.class);

    private final MenuJpaRepository menuJpaRepository;
    private final ImageStoragePort imageStoragePort;

    public MenuImageUploadListener(MenuJpaRepository menuJpaRepository,
                                   ImageStoragePort imageStoragePort) {
        this.menuJpaRepository = menuJpaRepository;
        this.imageStoragePort = imageStoragePort;
    }

    @Async
    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleImageUpload(MenuImageUploadRequestedEvent event) {
        logger.info("Procesando imagen para idMenu: {}", event.idMenu());

        try {
            if (!menuJpaRepository.existsByIdMenu(event.idMenu())) {
                logger.error("Menu no encontrado: idMenu={}", event.idMenu());
                return;
            }

            FileData fileData = new FileData(
                    event.originalFileName(),
                    event.contentType(),
                    event.fileBytes()
            );

            if (!fileData.isValid()) {
                logger.error("FileData inválido para idMenu: {}", event.idMenu());
                return;
            }

            ImageUploadResult result = imageStoragePort.uploadImage(fileData, "menus");

            if (result == null) {
                logger.error("S3 upload retornó null para idMenu: {}", event.idMenu());
                return;
            }

            String imageKey = result.imageKey();
            String imageUrl = imageStoragePort.getImageUrl(imageKey);

            logger.info("S3 upload exitoso: {}", imageUrl);

            int updated = menuJpaRepository.updateImageFields(
                    event.idMenu(),
                    imageUrl,
                    imageKey,
                    event.uploadedBy()
            );

            if (updated > 0) {
                logger.info("Menu {} actualizado con imagen", event.idMenu());
            } else {
                logger.warn("No se actualizó Menu {}", event.idMenu());
            }

        } catch (Exception e) {
            logger.error("Error procesando imagen para Menu {}: {}",
                    event.idMenu(), e.getMessage(), e);
        }
    }
}