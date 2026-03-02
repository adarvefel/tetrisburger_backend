package com.tetris.tetrisburger_backend.application.usecase.addition;

import com.tetris.tetrisburger_backend.domain.exception.AdditionAlreadyDeletedException;
import com.tetris.tetrisburger_backend.domain.model.Addition;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.DeleteAddition;
import com.tetris.tetrisburger_backend.domain.port.out.AdditionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class DeleteAdditionUseCase implements DeleteAddition {
    private final AdditionRepository repository;


    public DeleteAdditionUseCase(AdditionRepository repository) {
        this.repository = repository;
    }


    @Override
    public Addition handle(Integer id) {
        Addition addition = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adición no encontrada: " + id));


        if (addition.isDeleted()) {
            throw new AdditionAlreadyDeletedException("La adición ya fue eliminada");
        }

        addition.markAsDeleted();
        return repository.save(addition);

    }

}
