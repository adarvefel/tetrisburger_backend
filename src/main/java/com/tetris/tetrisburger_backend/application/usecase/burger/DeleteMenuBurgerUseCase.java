package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.DeleteMenuBurger;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DeleteMenuBurgerUseCase implements DeleteMenuBurger {

    private static final Logger logger = LoggerFactory.getLogger(DeleteMenuBurgerUseCase.class);

    private final BurgerRepository burgerRepository;

    public DeleteMenuBurgerUseCase(BurgerRepository burgerRepository) {
        this.burgerRepository = burgerRepository;
    }

    @Override
    public void handle(Integer idBurger) {
        logger.info("Eliminando burger de menú id={}", idBurger);

        Burger burger = burgerRepository.findActiveMenuById(idBurger)
                .orElseThrow(() -> new IllegalArgumentException("Burger de menú no encontrada"));

        burger.markAsDeleted();

        burgerRepository.save(burger);
    }
}
