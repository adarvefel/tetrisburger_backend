package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.exception.*;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.BurgerIngredient;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.burger.UpdateCustomBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateCustomBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class UpdateCustomBurgerUseCase implements UpdateCustomBurger {

    private static final Logger logger = LoggerFactory.getLogger(UpdateCustomBurgerUseCase.class);

    private final BurgerRepository burgerRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public UpdateCustomBurgerUseCase(
            BurgerRepository burgerRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.burgerRepository = burgerRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Burger handle(UpdateCustomBurgerCommand command) {
        logger.info("Updating custom burger: burgerId={}, userId={}",
                command.idBurger(), command.idUser());

        try {
            // 1. Validaciones
            validateCommand(command);
            validateUser(command.idUser());

            // 2. Cargar burger y validar dueño + que sea custom
            Burger burger = burgerRepository.findCustomByIdAndUser(
                            command.idBurger(), command.idUser())
                    .orElseThrow(() -> new BurgerNotFoundException(
                            "Hamburguesa no encontrada o no pertenece al usuario. ID: " + command.idBurger()
                    ));

            // 3. Validar que es custom burger
            if (!burger.isCustomBurger()) {
                throw new InvalidBurgerException(
                        "Solo hamburguesas personalizadas pueden actualizarse con este método. Burger ID: "
                                + command.idBurger()
                );
            }

            logger.debug("Current burger state: name={}, ingredients={}, finalPrice={}",
                    burger.getName(), burger.getIngredients().size(), burger.getFinalPrice());

            // 4. Crear nuevos ingredientes con validaciones
            List<BurgerIngredient> newIngredients = command.ingredients().stream()
                    .map(req -> createBurgerIngredient(req.idProduct(), req.quantity(), req.isOptional()))
                    .toList();

            // 5. Delegar la actualización al dominio
            burger.updateCustomBurger(
                    command.idUser(),
                    command.name(),
                    command.description(),
                    newIngredients
            );

            logger.debug("Updated burger state: name={}, ingredients={}, finalPrice={}",
                    burger.getName(), burger.getIngredients().size(), burger.getFinalPrice());

            // 6. Guardar
            Burger updated = burgerRepository.save(burger);

            if (updated == null) {
                throw new BurgerCreationException("Failed to save updated custom burger");
            }

            logger.info("Custom burger updated successfully: burgerId={}, userId={}",
                    updated.getIdBurger(), command.idUser());

            // La imagen se actualiza con UpdateCustomBurgerImageUseCase

            return updated;

        } catch (BurgerNotFoundException | UserNotFoundException |
                 ProductNotFoundException | InsufficientStockException |
                 InvalidBurgerException e) {
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidBurgerException(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error updating custom burger: burgerId={}",
                    command.idBurger(), e);
            throw new BurgerCreationException("Error updating custom burger", e);
        }
    }

    private void validateCommand(UpdateCustomBurgerCommand command) {
        if (command == null) {
            throw new InvalidBurgerException("Command cannot be null");
        }

        if (command.idBurger() == null) {
            throw new InvalidBurgerException("Burger ID cannot be null");
        }

        if (command.idUser() == null) {
            throw new InvalidBurgerException("User ID cannot be null");
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

    private void validateUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }

    private BurgerIngredient createBurgerIngredient(Integer productId, Integer quantity, Boolean isOptional) {
        logger.debug("Creating burger ingredient: productId={}, quantity={}", productId, quantity);

        if (productId == null) {
            throw new InvalidBurgerException("Product ID cannot be null");
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

        if (product.getStock() != null && product.getStock() < quantity) {
            throw new InsufficientStockException(
                    product.getName(),
                    quantity,
                    product.getStock()
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
