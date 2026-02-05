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
        validateInput(idBurger, newPrice);

        // 2. Buscar burger activa de menú
        Burger burger = burgerRepository.findActiveMenuById(idBurger)
                .orElseThrow(() -> new BurgerNotFoundException(
                        "Hamburguesa de menú no encontrada o inactiva: " + idBurger
                ));

        // 3. Validar que sea burger de menú (redundante pero seguro)
        if (!burger.isOnMenu()) {
            throw new InvalidBurgerException(
                    "Solo hamburguesas de menú pueden actualizar precio. ID: " + idBurger
            );
        }

        // 4. Obtener precios actuales para logging
        BigDecimal oldPrice = burger.getFinalPrice();
        BigDecimal basePrice = burger.getBasePrice();

        // 5. Actualizar solo el finalPrice (precio de venta)
        // El basePrice (costo de ingredientes) NO cambia
        burger.setFinalPrice(newPrice);

        // 6. Calcular y loggear información útil
        BigDecimal priceDifference = newPrice.subtract(basePrice);
        BigDecimal margin = calculateMarginPercentage(basePrice, newPrice);

        logger.info(" Detalles del cambio de precio:");
        logger.info("  • Precio anterior: ${}", oldPrice);
        logger.info("  • Precio nuevo: ${}", newPrice);
        logger.info("  • Costo base (ingredientes): ${}", basePrice);
        logger.info("  • Diferencia vs costo: ${} ({}%)", priceDifference, margin);

        // 7. Validar margen razonable (opcional)
        validatePriceMargin(basePrice, newPrice);

        // 8. Guardar (JPA Auditing llenará updatedBy y updatedAt)
        Burger updated = burgerRepository.save(burger);

        logger.info(" Precio de hamburguesa actualizado exitosamente: idBurger={}", idBurger);

        return updated;
    }

    /**
     * Valida los parámetros de entrada
     */
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

    /**
     * Valida que el margen de precio sea razonable
     * Permite desde -50% (promoción) hasta +300% (premium)
     */
    private void validatePriceMargin(BigDecimal basePrice, BigDecimal finalPrice) {
        if (basePrice.compareTo(BigDecimal.ZERO) == 0) {
            return; // Evitar división por cero
        }

        BigDecimal margin = finalPrice.subtract(basePrice)
                .divide(basePrice, 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        // Permitir desde -50% (promoción agresiva) hasta +300% (premium extremo)
        if (margin.compareTo(BigDecimal.valueOf(-50)) < 0) {
            logger.warn(" ADVERTENCIA: Precio con descuento mayor al 50%: {}%", margin);
            throw new InvalidBurgerException(
                    String.format("El precio no puede ser menor al 50%% del costo base. " +
                                    "Costo: $%s, Precio propuesto: $%s (descuento: %.1f%%)",
                            basePrice, finalPrice, margin.abs().doubleValue())
            );
        }

        if (margin.compareTo(BigDecimal.valueOf(300)) > 0) {
            logger.warn("⚠ ADVERTENCIA: Precio con margen mayor al 300%: {}%", margin);
            throw new InvalidBurgerException(
                    String.format("El precio no puede ser mayor al 300%% del costo base. " +
                                    "Costo: $%s, Precio propuesto: $%s (margen: %.1f%%)",
                            basePrice, finalPrice, margin.doubleValue())
            );
        }

        // Advertencia si el precio es menor al costo (pérdida)
        if (finalPrice.compareTo(basePrice) < 0) {
            logger.warn("⚠ ADVERTENCIA: Precio de venta menor al costo base. " +
                    "Se está vendiendo con pérdida: ${} < ${}", finalPrice, basePrice);
        }
    }

    /**
     * Calcula el margen de ganancia en porcentaje
     */
    private BigDecimal calculateMarginPercentage(BigDecimal basePrice, BigDecimal finalPrice) {
        if (basePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return finalPrice.subtract(basePrice)
                .divide(basePrice, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
