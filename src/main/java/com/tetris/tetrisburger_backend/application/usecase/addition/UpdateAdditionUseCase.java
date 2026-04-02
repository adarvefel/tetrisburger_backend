package com.tetris.tetrisburger_backend.application.usecase.addition;

import com.tetris.tetrisburger_backend.domain.exception.AdditionAlreadyDeletedException;
import com.tetris.tetrisburger_backend.domain.exception.AdditionNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Addition;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.UpdateAddition;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.command.UpdateAdditionCommand;
import com.tetris.tetrisburger_backend.domain.port.out.AdditionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
@Transactional
public class UpdateAdditionUseCase implements UpdateAddition {

    private final AdditionRepository additionRepository;

    public UpdateAdditionUseCase(AdditionRepository additionRepository) {
        this.additionRepository = additionRepository;
    }

    @Override
    public Addition handle(UpdateAdditionCommand cmd) {
        Addition addition = additionRepository.findById(cmd.id())
                .orElseThrow(() -> new AdditionNotFoundException(
                        "Adición no encontrada con ID: " + cmd.id()
                ));

        if (addition.isDeleted()) {
            throw new AdditionAlreadyDeletedException(
                    "La adición ya fue eliminada. ID: " + cmd.id()
            );
        }

        String newName = cmd.name() != null ? cmd.name().trim() : "";
        boolean nameChanged = !newName.equalsIgnoreCase(addition.getName());

        if (nameChanged && additionRepository.existsByNameIgnoreCase(newName)) {
            throw new IllegalArgumentException("Ya existe una adición con el nombre: " + newName);
        }

        addition.update(
                cmd.name(),
                cmd.description(),
                cmd.price(),
                cmd.available(),
                null,
                cmd.updatedBy()
        );

        return additionRepository.save(addition);
    }
}
