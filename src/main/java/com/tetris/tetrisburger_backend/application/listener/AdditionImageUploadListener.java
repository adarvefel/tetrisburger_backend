package com.tetris.tetrisburger_backend.application.listener;

import com.tetris.tetrisburger_backend.application.event.AdditionImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;
import com.tetris.tetrisburger_backend.domain.exception.ImageUploadException;
import com.tetris.tetrisburger_backend.domain.model.Addition;
import com.tetris.tetrisburger_backend.domain.port.out.AdditionRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AdditionImageUploadListener {

    private final AdditionRepository additionRepository;
    private final ImageStoragePort imageStoragePort;

    public AdditionImageUploadListener(
            AdditionRepository additionRepository,
            ImageStoragePort imageStoragePort
    ) {
        this.additionRepository = additionRepository;
        this.imageStoragePort = imageStoragePort;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAdditionImageUploadRequested(AdditionImageUploadRequestedEvent event) {
        try {
            FileData fileData = new FileData(
                    event.originalFilename(),
                    event.contentType(),
                    event.imageBytes()
            );

            ImageUploadResult upload = imageStoragePort.uploadAdditionImage(fileData);

            if (upload == null) {
                return;
            }

            persistAdditionImage(event.additionId(), upload,event.userId());

        } catch (Exception e) {
            throw new ImageUploadException("No se pudo subir la imagen de la adición", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void persistAdditionImage(Integer additionId, ImageUploadResult upload,Integer updatedBy) {
        Addition addition = additionRepository.findById(additionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Adición no encontrada con ID: " + additionId
                ));

        String imageUrl = imageStoragePort.getImageUrl(upload.imageKey());
        addition.updateImage(upload.imageKey(), imageUrl,updatedBy);

        additionRepository.save(addition);
    }
}
