package com.tetris.tetrisburger_backend.application.usecase.addition;

import com.tetris.tetrisburger_backend.domain.exception.AdditionNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Addition;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.GetAdditionById;
import com.tetris.tetrisburger_backend.domain.port.out.AdditionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class GetAdditionByIdUseCase implements GetAdditionById {

    private final AdditionRepository additionRepository;

    public GetAdditionByIdUseCase(AdditionRepository additionRepository) {
        this.additionRepository = additionRepository;
    }

    @Override
    public Addition execute(Integer id) {
        Addition addition = additionRepository.findById(id)
                .orElseThrow(() -> new AdditionNotFoundException(
                        "No se encontro la adiccion con el ID: " + id
                ));

        if (addition.isDeleted()) {
            throw new AdditionNotFoundException(
                    "La adición con ID " + id + " ha sido eliminada"
            );
        }

        return addition;
    }
}
