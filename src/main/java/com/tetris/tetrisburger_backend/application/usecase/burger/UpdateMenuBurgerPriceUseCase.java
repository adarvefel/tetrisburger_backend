package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.exception.BurgerNotFoundException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidBurgerException;
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

    private static final Logger logger = LoggerFactory.getLogger(UpdateMenuBurgerPriceUseCase.class);

    private final BurgerRepository burgerRepository;

    public UpdateMenuBurgerPriceUseCase(BurgerRepository burgerRepository) {
        this.burgerRepository = burgerRepository;
    }

    @Override
    public Burger handle(Integer idBurger, BigDecimal newPrice) {
        logger.info("Actualizando precio de hamburguesa de menú: idBurger={}, newPrice={}",
                idBurger, newPrice);

        // 1. Validaciones
        if (idBurger == null) {
            throw new InvalidBurgerException("El ID de la hamburguesa no puede ser nulo");
        }

        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidBurgerException("El precio debe ser mayor a 0");
        }

        // 2. Buscar burger
        Burger burger = burgerRepository.findById(idBurger)
                .orElseThrow(() -> new BurgerNotFoundException(idBurger));

        // 3. Validar que sea burger de menú
        if (!burger.isOnMenu()) {
            throw new InvalidBurgerException(
                    "Solo hamburguesas de menú pueden actualizar precio. ID: " + idBurger
            );
        }

        // 4. Actualizar precio (null = JPA Auditing automático)
        burger.updatePrice(newPrice, null);

        // 5. Guardar (JPA Auditing llenará updatedBy y updatedAt)
        Burger updated = burgerRepository.save(burger);

        logger.info("Precio de hamburguesa de menú actualizado exitosamente: idBurger={}", idBurger);

        return updated;
    }
}
