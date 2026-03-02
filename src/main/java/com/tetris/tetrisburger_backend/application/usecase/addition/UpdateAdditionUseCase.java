// UpdateAdditionUseCase.java
package com.tetris.tetrisburger_backend.application.usecase.addition;

import com.tetris.tetrisburger_backend.domain.exception.AdditionAlreadyDeletedException;
import com.tetris.tetrisburger_backend.domain.exception.AdditionNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Addition;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.UpdateAddition;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.command.UpdateAdditionCommand;
import com.tetris.tetrisburger_backend.domain.port.out.AdditionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@Transactional
public class UpdateAdditionUseCase implements UpdateAddition {

    private static final Logger logger = LoggerFactory.getLogger(UpdateAdditionUseCase.class);

    private final AdditionRepository additionRepository;

    public UpdateAdditionUseCase(AdditionRepository additionRepository) {
        this.additionRepository = additionRepository;
    }

    @Override
    public Addition handle(UpdateAdditionCommand cmd) {
        logger.info("Actualizando adición ID: {}", cmd.id());

        Addition addition = additionRepository.findById(cmd.id())
                .orElseThrow(() -> new AdditionNotFoundException(
                        "Adición no encontrada con ID: " + id
                ));

        if (addition.isDeleted()) {
            throw new AdditionAlreadyDeletedException(
                    "La adición ya fue eliminada. ID: " + id
            );
        }

        String newName = cmd.name() != null ? cmd.name().trim() : "";
        boolean nameChanged = !newName.equalsIgnoreCase(addition.getName());

        if (nameChanged && additionRepository.existsByNameIgnoreCase(newName)) {
            logger.warn("RECHAZADO - Nombre duplicado: '{}'", newName);
            throw new IllegalArgumentException("Ya existe una adición con el nombre: " + newName);
        }

        addition.update(
                cmd.name(),
                cmd.description(),
                cmd.price(),
                cmd.available(),
                null
                // imageUrl no se toca aquí
        );

        Addition saved = additionRepository.save(addition);
        logger.info("Adición ID {} actualizada", saved.getIdAddition());

        return saved;
    }
}
