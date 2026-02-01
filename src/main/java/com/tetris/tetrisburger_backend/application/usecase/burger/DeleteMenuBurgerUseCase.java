package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.exception.BurgerNotFoundException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidBurgerException;
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
    public void handle(Integer idBurger, Integer deletedBy) {
        logger.info("Eliminando burger de menú: idBurger={}, adminUserId={}",
                idBurger, deletedBy);

        Burger burger = burgerRepository.findActiveMenuById(idBurger)
                .orElseThrow(() -> new BurgerNotFoundException(
                        "Burger no encontrada con ID: " + idBurger
                ));

        burger.markAsDeleted(deletedBy);
        burgerRepository.save(burger);

        logger.info("Burger de menú eliminada: idBurger={}, nombre={}",
                burger.getIdBurger(), burger.getName());
    }

}
