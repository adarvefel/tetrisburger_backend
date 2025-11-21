package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.UpdateMenuBurgerPrice;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Transactional
public class UpdateMenuBurgerPriceUseCase implements UpdateMenuBurgerPrice {

    private static final Logger logger =
            LoggerFactory.getLogger(UpdateMenuBurgerPriceUseCase.class);

    private final BurgerRepository burgerRepository;

    public UpdateMenuBurgerPriceUseCase(BurgerRepository burgerRepository) {
        this.burgerRepository = burgerRepository;
    }

    @Override
    public Burger handle(Integer idBurger, BigDecimal newPrice) {
        logger.info("Actualizando precio de burger de menú id={} a {}", idBurger, newPrice);

        Burger burger = burgerRepository.findActiveMenuById(idBurger)
                .orElseThrow(() -> new IllegalArgumentException("Burger de menú no encontrada"));

        burger.updatePrice(newPrice);

        Burger updated = burgerRepository.save(burger);

        logger.info("Precio actualizado de burger de menú id={} a {}", updated.getIdBurger(), updated.getBasePrice());

        return updated;
    }
}
