package com.tetris.tetrisburger_backend.application.usecase.addition;

import com.tetris.tetrisburger_backend.application.event.AdditionImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.model.Addition;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.UpdateAdditionImage;
import com.tetris.tetrisburger_backend.domain.port.out.AdditionRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class UpdateImageAdditionUseCase implements UpdateAdditionImage {

    private final AdditionRepository  repository;
    private final ApplicationEventPublisher eventPublisher;

    public UpdateImageAdditionUseCase(AdditionRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    @Override
    public Addition handle(Integer id, FileData additionImage,Integer updatedBy) {
        if (additionImage == null){
            throw  new IllegalArgumentException("La imagen de la adicion es requerida");

        }
        String contentType = additionImage.contentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen");
        }



        Addition addition = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Adicion no encontrada con ID: " + id));

        eventPublisher.publishEvent(new AdditionImageUploadRequestedEvent(
                id,
                additionImage.bytes(),
                additionImage.contentType(),
                additionImage.originalFilename(),
                updatedBy
        ));

        return addition;




    }


}
