package com.tetris.tetrisburger_backend.application.listener;

import com.tetris.tetrisburger_backend.application.event.MenuBurgerImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;
import com.tetris.tetrisburger_backend.domain.exception.BurgerNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.BurgerJpaRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Component
public class MenuBurgerImageUploadListener {

    private static final Logger logger = LoggerFactory.getLogger(MenuBurgerImageUploadListener.class);

    private final BurgerJpaRepository burgerJpaRepository;
    private final ImageStoragePort imageStoragePort;

    public MenuBurgerImageUploadListener(
            BurgerJpaRepository burgerJpaRepository,
            ImageStoragePort imageStoragePort) {
        this.burgerJpaRepository = burgerJpaRepository;
        this.imageStoragePort = imageStoragePort;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleImageUpload(MenuBurgerImageUploadRequestedEvent event) {
        logger.info(" Procesando evento de imagen para idBurger: {}", event.idBurger());

        try {
            // 1. Verificar existencia (sin cargar entity)
            if (!burgerJpaRepository.existsByIdBurger(event.idBurger())) {
                logger.error(" Burger no encontrado: idBurger={}", event.idBurger());
                return;
            }

            // 2. Crear FileData
            FileData fileData = new FileData(
                    event.originalFileName(),
                    event.contentType(),
                    event.fileBytes()
            );

            if (!fileData.isValid()) {
                logger.error(" FileData inválido para idBurger: {}", event.idBurger());
                return;
            }

            logger.info(" FileData: {} ({} bytes)",
                    fileData.originalFilename(), fileData.bytes().length);

            // 3. Subir a S3
            ImageUploadResult result = imageStoragePort.uploadImage(fileData, "burgers");

            if (result == null) {
                logger.error(" S3 upload retornó null para idBurger: {}", event.idBurger());
                return;
            }

            // 4. Generar URL
            String imageKey = result.imageKey();
            String imageUrl = imageStoragePort.getImageUrl(imageKey);

            logger.info(" S3 upload exitoso: {}", imageUrl);

            // 5.  Actualizar solo campos de imagen (sin cargar ingredients)
            int updated = burgerJpaRepository.updateImageFields(
                    event.idBurger(),
                    imageUrl,
                    imageKey,
                    event.uploadedBy()
            );

            if (updated > 0) {
                logger.info(" Burger {} actualizada con imagen", event.idBurger());
            } else {
                logger.warn(" No se actualizó burger {}", event.idBurger());
            }

        } catch (Exception e) {
            logger.error(" Error procesando imagen para burger {}: {}",
                    event.idBurger(), e.getMessage(), e);
        }
    }
}
