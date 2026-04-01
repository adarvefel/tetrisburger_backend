package com.tetris.tetrisburger_backend.application.usecase.addition;

import com.tetris.tetrisburger_backend.application.event.AdditionImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.exception.AdditionAlreadyExistsException;
import com.tetris.tetrisburger_backend.domain.model.Addition;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.CreateAddition;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.command.CreateAdditionCommand;
import com.tetris.tetrisburger_backend.domain.port.out.AdditionRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CreateAdditionUseCase implements CreateAddition {

    private final AdditionRepository additionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CreateAdditionUseCase(
            AdditionRepository additionRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.additionRepository = additionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Addition handle(CreateAdditionCommand cmd) {
        String name = cmd.name() != null ? cmd.name().trim() : "";

        if (additionRepository.existsByNameIgnoreCase(name)) {
            throw new AdditionAlreadyExistsException(
                    "Ya existe una adición con el nombre: " + cmd.name()
            );
        }

        Addition addition = Addition.create(
                name,
                cmd.description(),
                cmd.price(),
                cmd.available(),
                null,
                null,
                cmd.createdBy()

        );

        Addition saved = additionRepository.save(addition);

        if (cmd.additionImage() != null) {
            eventPublisher.publishEvent(new AdditionImageUploadRequestedEvent(
                    saved.getIdAddition(),
                    cmd.additionImage().bytes(),
                    cmd.additionImage().contentType(),
                    cmd.additionImage().originalFilename(),
                    cmd.createdBy()

            ));
        }

        return saved;
    }
}
