package com.tetris.tetrisburger_backend.application.usecase.burger.admin;

import com.tetris.tetrisburger_backend.domain.exception.BurgerNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.DeleteMenuBurger;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DeleteMenuBurgerUseCase implements DeleteMenuBurger {

    private final BurgerRepository burgerRepository;

    public DeleteMenuBurgerUseCase(BurgerRepository burgerRepository) {
        this.burgerRepository = burgerRepository;
    }

    @Override
    public void handle(Integer idBurger, Integer deletedBy) {
        Burger burger = burgerRepository.findActiveMenuById(idBurger)
                .orElseThrow(() -> new BurgerNotFoundException(
                        "Burger no encontrada con ID: " + idBurger
                ));

        burger.markAsDeleted(deletedBy);
        burgerRepository.save(burger);
    }
}
