package com.tetris.tetrisburger_backend.application.usecase.burger.user;

import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.user.UnmarkCustomBurgerAsFavorite;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UnmarkCustomBurgerAsFavoriteUseCase implements UnmarkCustomBurgerAsFavorite {

    private static final Logger logger =
            LoggerFactory.getLogger(UnmarkCustomBurgerAsFavoriteUseCase.class);

    private final BurgerRepository burgerRepository;

    public UnmarkCustomBurgerAsFavoriteUseCase(BurgerRepository burgerRepository) {
        this.burgerRepository = burgerRepository;
    }

    @Override
    public void handle(Integer idBurger, Integer idUser) {
        logger.info("Desmarcando burger custom id={} como favorita para user={}",
                idBurger, idUser);

        Burger burger = burgerRepository
                .findCustomByIdAndUser(idBurger, idUser)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Burger no encontrada o no pertenece al usuario"));

        burger.unmarkAsFavorite(idUser);

        burgerRepository.save(burger);

        logger.info("Burger custom id={} desmarcada como favorita", idBurger);
    }
}
