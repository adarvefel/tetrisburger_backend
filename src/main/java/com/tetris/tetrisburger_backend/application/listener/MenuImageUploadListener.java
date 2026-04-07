package com.tetris.tetrisburger_backend.application.listener;

import com.tetris.tetrisburger_backend.application.event.MenuImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.MenuJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class MenuImageUploadListener {

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
        try {
            if (!menuJpaRepository.existsByIdMenu(event.idMenu())) {
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

            ImageUploadResult result = imageStoragePort.uploadImage(fileData, "menus");

            if (result == null) {
                return;
            }

            String imageKey = result.imageKey();
            String imageUrl = imageStoragePort.getImageUrl(imageKey);

            menuJpaRepository.updateImageFields(
                    event.idMenu(),
                    imageUrl,
                    imageKey,
                    event.uploadedBy()
            );

        } catch (Exception e) {

        }
    }
}
