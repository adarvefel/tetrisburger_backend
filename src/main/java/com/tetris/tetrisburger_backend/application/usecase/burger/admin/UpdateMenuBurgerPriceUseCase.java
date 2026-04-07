package com.tetris.tetrisburger_backend.application.usecase.burger.admin;

import com.tetris.tetrisburger_backend.domain.exception.BurgerNotFoundException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidBurgerException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.UpdateMenuBurgerPrice;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Transactional
public class UpdateMenuBurgerPriceUseCase implements UpdateMenuBurgerPrice {

    private final BurgerRepository burgerRepository;

    public UpdateMenuBurgerPriceUseCase(BurgerRepository burgerRepository) {
        this.burgerRepository = burgerRepository;
    }

    @Override
    public Burger handle(Integer idBurger, BigDecimal newPrice) {
        validateInput(idBurger, newPrice);

        Burger burger = burgerRepository.findActiveMenuById(idBurger)
                .orElseThrow(() -> new BurgerNotFoundException(
                        "Hamburguesa de menú no encontrada o inactiva: " + idBurger
                ));

        if (!burger.isOnMenu()) {
            throw new InvalidBurgerException(
                    "Solo hamburguesas de menú pueden actualizar precio. ID: " + idBurger
            );
        }

        burger.setFinalPrice(newPrice);

        validatePriceMargin(burger.getBasePrice(), newPrice);

        return burgerRepository.save(burger);
    }

    private void validateInput(Integer idBurger, BigDecimal newPrice) {
        if (idBurger == null) {
            throw new InvalidBurgerException("El ID de la hamburguesa no puede ser nulo");
        }
        if (newPrice == null) {
            throw new InvalidBurgerException("El precio no puede ser nulo");
        }
        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidBurgerException("El precio debe ser mayor a 0");
        }
    }

    private void validatePriceMargin(BigDecimal basePrice, BigDecimal finalPrice) {
        if (basePrice.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        BigDecimal margin = finalPrice.subtract(basePrice)
                .divide(basePrice, 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        if (margin.compareTo(BigDecimal.valueOf(-50)) < 0) {
            throw new InvalidBurgerException(
                    String.format("El precio no puede ser menor al 50%% del costo base. " +
                                    "Costo: $%s, Precio propuesto: $%s (descuento: %.1f%%)",
                            basePrice, finalPrice, margin.abs().doubleValue())
            );
        }
    }

    private BigDecimal calculateMarginPercentage(BigDecimal basePrice, BigDecimal finalPrice) {
        if (basePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return finalPrice.subtract(basePrice)
                .divide(basePrice, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
