// src/main/java/com/tetris/tetrisburger_backend/application/usecase/burger/GetBurgerByIdUseCase.java
package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.GetBurgerById;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class GetBurgerByIdUseCase implements GetBurgerById {
    private final Logger logger = LoggerFactory.getLogger(GetBurgerByIdUseCase.class);

    private final BurgerRepository burgerRepository;

    public GetBurgerByIdUseCase(BurgerRepository burgerRepository) {
        this.burgerRepository = burgerRepository;
    }

    @Override

    public Burger   execute(Integer idBurger) {
        logger.info("Buscando hamburguesa por ID: {}", idBurger);
        return burgerRepository.findById(idBurger)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Hamburguesa no encontrada con id: " + idBurger
                ));


    }
}
