package com.tetris.tetrisburger_backend.application.usecase.addition;

import com.tetris.tetrisburger_backend.domain.exception.AdditionNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Addition;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.GetAdditionById;
import com.tetris.tetrisburger_backend.domain.port.out.AdditionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Transactional
public class GetAdditionByIdUseCase implements GetAdditionById {

    private static final Logger logger =
            LoggerFactory.getLogger(GetAdditionByIdUseCase.class);

    private final AdditionRepository additionRepository;

    public GetAdditionByIdUseCase(AdditionRepository additionRepository) {
        this.additionRepository = additionRepository;
    }

    @Override
    public Addition execute(Integer id) {
        logger.info("Buscando addition por ID: {}", id);

        Addition addition = additionRepository.findById(id)
                .orElseThrow(() -> new AdditionNotFoundException("No se encontro la adiccion con el ID: "+ id));

        if (addition.isDeleted()) {
            logger.warn("Intento de acceder a addition eliminada: ID={}", id);
            throw new AdditionNotFoundException(
                    "La adición con ID " + id + " ha sido eliminada"
            );
        }

        logger.info("Addition encontrada: ID={}, name={}",
                addition.getIdAddition(), addition.getName());

        return addition;
    }
}
