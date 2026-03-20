package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.exception.BurgerNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.GetBurgerById;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class GetBurgerByIdUseCase implements GetBurgerById {

    private static final Logger logger = LoggerFactory.getLogger(GetBurgerByIdUseCase.class);

    private final BurgerRepository burgerRepository;

    public GetBurgerByIdUseCase(BurgerRepository burgerRepository) {
        this.burgerRepository = burgerRepository;
    }

    @Override
    public Burger execute(Integer idBurger) {
        logger.info(" Buscando hamburguesa por ID: {}", idBurger);

        Burger burger = burgerRepository.findById(idBurger)
                .orElseThrow(() -> new BurgerNotFoundException(idBurger));

        // Validar que no esté eliminada
        if (burger.isDeleted()) {
            logger.warn(" Intento de acceder a burger eliminada: ID={}", idBurger);
            throw new BurgerNotFoundException(
                    "La hamburguesa con ID " + idBurger + " ha sido eliminada"
            );
        }

        logger.info("Hamburguesa encontrada: ID={}, name={}", burger.getIdBurger(), burger.getName());
        return burger;
    }
}
