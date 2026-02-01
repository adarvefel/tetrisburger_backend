package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.exception.BurgerCreationException;
import com.tetris.tetrisburger_backend.domain.exception.BurgerNotFoundException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidBurgerException;
import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.BurgerIngredient;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.burger.UpdateMenuBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateMenuBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class UpdateMenuBurgerUseCase implements UpdateMenuBurger {

    private static final Logger logger = LoggerFactory.getLogger(UpdateMenuBurgerUseCase.class);

    private final BurgerRepository burgerRepository;
    private final ProductRepository productRepository;

    public UpdateMenuBurgerUseCase(BurgerRepository burgerRepository,
                                   ProductRepository productRepository) {
        this.burgerRepository = burgerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Burger handle(UpdateMenuBurgerCommand command) {
        logger.info("Actualizando hamburguesa de menú: idBurger={}", command.idBurger());

        try {
            // 1. Validaciones
            validateCommand(command);

            // 2. Buscar burger de menú
            Burger burger = burgerRepository.findActiveMenuById(command.idBurger())
                    .orElseThrow(() -> new BurgerNotFoundException(
                            "Hamburguesa de menú no encontrada o ya eliminada. ID: " + command.idBurger()
                    ));

            // 3. Validar que sea burger de menú
            if (!burger.isOnMenu()) {
                throw new InvalidBurgerException(
                        "La hamburguesa no es una hamburguesa de menú. ID: " + command.idBurger()
                );
            }

            if (burger.isCustomBurger()) {
                throw new InvalidBurgerException(
                        "No se puede actualizar una hamburguesa personalizada con este método. ID: " + command.idBurger()
                );
            }

            logger.debug("Estado actual de la hamburguesa: name={}, ingredients={}, finalPrice={}",
                    burger.getName(), burger.getIngredients().size(), burger.getFinalPrice());

            // 4. Crear nuevos ingredientes con validaciones
            List<BurgerIngredient> newIngredients = command.ingredients().stream()
                    .map(req -> createBurgerIngredient(req.idProduct(), req.quantity(), req.isOptional()))
                    .toList();

            // 5. Delegar la actualización al dominio
            burger.updateMenuBurger(
                    command.name(),
                    command.description(),
                    newIngredients,
                    command.availability(),
                    command.isOnMenu(),
                    command.favorite(),
                    command.updatedBy()
            );

            logger.debug("Estado actualizado de la hamburguesa: name={}, ingredients={}, finalPrice={}",
                    burger.getName(), burger.getIngredients().size(), burger.getFinalPrice());

            // 6. Guardar
            Burger updated = burgerRepository.save(burger);

            if (updated == null) {
                throw new BurgerCreationException("Error al guardar la hamburguesa de menú actualizada");
            }

            logger.info("Hamburguesa de menú actualizada exitosamente: burgerId={}", updated.getIdBurger());

            return updated;

        } catch (BurgerNotFoundException | ProductNotFoundException | InvalidBurgerException e) {
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidBurgerException(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado actualizando hamburguesa de menú: burgerId={}",
                    command.idBurger(), e);
            throw new BurgerCreationException("Error actualizando hamburguesa de menú", e);
        }
    }

    private void validateCommand(UpdateMenuBurgerCommand command) {
        if (command == null) {
            throw new InvalidBurgerException("El comando no puede ser nulo");
        }

        if (command.idBurger() == null) {
            throw new InvalidBurgerException("El ID de la hamburguesa no puede ser nulo");
        }

        if (command.name() == null || command.name().isBlank()) {
            throw new InvalidBurgerException("El nombre no puede estar vacío");
        }

        if (command.ingredients() == null || command.ingredients().isEmpty()) {
            throw new InvalidBurgerException(
                    "La hamburguesa debe tener al menos un ingrediente"
            );
        }
    }

    private BurgerIngredient createBurgerIngredient(Integer productId, Integer quantity, Boolean isOptional) {
        logger.debug("Creando ingrediente: productId={}, quantity={}", productId, quantity);

        if (productId == null) {
            throw new InvalidBurgerException("El ID del producto no puede ser nulo");
        }

        if (quantity == null || quantity <= 0) {
            throw new InvalidBurgerException("La cantidad debe ser mayor a 0");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (!product.isAvailable()) {
            throw new InvalidBurgerException(
                    "El producto '" + product.getName() + "' no está disponible"
            );
        }

        BigDecimal priceAtTime = product.getPrice();

        return BurgerIngredient.create(
                productId,
                priceAtTime,
                quantity,
                isOptional != null ? isOptional : false
        );
    }
}
