package com.tetris.tetrisburger_backend.application.listener;

import com.tetris.tetrisburger_backend.application.event.MenuBurgerImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.BurgerJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class MenuBurgerImageUploadListener {

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
        try {
            if (!burgerJpaRepository.existsByIdBurger(event.idBurger())) {
                return;
            }

            FileData fileData = new FileData(
                    event.originalFileName(),
                    event.contentType(),
                    event.fileBytes()
            );

            if (!fileData.isValid()) {
                return;
            }

            ImageUploadResult result = imageStoragePort.uploadImage(fileData, "burgers");

            if (result == null) {
                return;
            }

            String imageKey = result.imageKey();
            String imageUrl = imageStoragePort.getImageUrl(imageKey);

            burgerJpaRepository.updateImageFields(
                    event.idBurger(),
                    imageUrl,
                    imageKey,
                    event.uploadedBy()
            );

        } catch (Exception e) {
            // handle silently
        }
    }
}
