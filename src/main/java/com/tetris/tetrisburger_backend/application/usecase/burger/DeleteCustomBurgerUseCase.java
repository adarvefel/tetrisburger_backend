package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.DeleteCustomBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.DeleteCustomBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DeleteCustomBurgerUseCase implements DeleteCustomBurger {

    private static final Logger logger = LoggerFactory.getLogger(DeleteCustomBurgerUseCase.class);

    private final BurgerRepository burgerRepository;

    public DeleteCustomBurgerUseCase(BurgerRepository burgerRepository) {
        this.burgerRepository = burgerRepository;
    }

    @Override
    public void handle(DeleteCustomBurgerCommand command) {
        logger.info("Eliminando (soft) burger custom id={} para user={}",
                command.idBurger(), command.idUser());

        Burger burger = burgerRepository.findCustomByIdAndUser(
                        command.idBurger(), command.idUser())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Burger no encontrada o no pertenece al usuario"));

        // Soft delete a través del dominio
        burger.markCustomAsDeleted(command.idUser());

        burgerRepository.save(burger);

        logger.info("Burger custom id={} marcada como eliminada para user={}",
                command.idBurger(), command.idUser());
    }
}
