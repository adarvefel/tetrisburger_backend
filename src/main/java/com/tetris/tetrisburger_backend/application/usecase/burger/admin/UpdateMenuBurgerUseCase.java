package com.tetris.tetrisburger_backend.application.usecase.burger.admin;

import com.tetris.tetrisburger_backend.domain.exception.BurgerCreationException;
import com.tetris.tetrisburger_backend.domain.exception.BurgerNotFoundException;
import com.tetris.tetrisburger_backend.domain.exception.InsufficientStockException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidBurgerException;
import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.BurgerIngredient;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.enums.ProductType;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.UpdateMenuBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateMenuBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UpdateMenuBurgerUseCase implements UpdateMenuBurger {

    private static final Set<ProductType> ALLOWED_INGREDIENT_TYPES = Set.of(
            ProductType.INGREDIENT
    );

    private final BurgerRepository burgerRepository;
    private final ProductRepository productRepository;

    public UpdateMenuBurgerUseCase(BurgerRepository burgerRepository,
                                   ProductRepository productRepository) {
        this.burgerRepository = burgerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Burger handle(UpdateMenuBurgerCommand command) {

        try {
            validateCommand(command);

            Burger burger = burgerRepository.findById(command.idBurger())
                    .orElseThrow(() -> new BurgerNotFoundException(
                            "Hamburguesa no encontrada. ID: " + command.idBurger()
                    ));

            validateIsMenuBurger(burger);

            validateNoDuplicateIngredients(command.ingredients());

            List<BurgerIngredient> newIngredients = command.ingredients().stream()
                    .map(req -> {
                        Product product = productRepository.findById(req.idProduct())
                                .orElseThrow(() -> new ProductNotFoundException(req.idProduct()));

                        validateProductForBurger(product);

                        ProductSnapshot snapshot = ProductSnapshot.fromProduct(
                                product,
                                req.quantity(),
                                false
                        );

                        return BurgerIngredient.fromSnapshot(snapshot);
                    })
                    .toList();


            burger.updateMenuBurger(
                    command.name(),
                    command.description(),
                    newIngredients,
                    command.availability(),
                    command.isFeatured(),
                    command.updatedBy()
            );

            if (command.finalPrice() != null) {
                burger.updateFinalPrice(command.finalPrice(), command.updatedBy());
            }


            Burger updated = burgerRepository.save(burger);

            if (updated == null) {
                throw new BurgerCreationException("Error al guardar la hamburguesa de menú actualizada");
            }

            return updated;

        } catch (BurgerNotFoundException | ProductNotFoundException |
                 InvalidBurgerException | InsufficientStockException e) {
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidBurgerException(e.getMessage());
        } catch (Exception e) {
            throw new BurgerCreationException("Error actualizando hamburguesa de menú", e);
        }
    }

    // ==================== VALIDACIONES ====================

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

        if (command.updatedBy() == null) {
            throw new InvalidBurgerException("El ID del usuario que actualiza es obligatorio");
        }
    }

    private void validateIsMenuBurger(Burger burger) {
        if (!burger.isOnMenu()) {
            throw new InvalidBurgerException(
                    "La hamburguesa no es una hamburguesa de menú. ID: " + burger.getIdBurger()
            );
        }

        if (burger.isDeleted()) {
            throw new InvalidBurgerException(
                    "No se puede actualizar una hamburguesa eliminada. ID: " + burger.getIdBurger()
            );
        }
    }

    private void validateNoDuplicateIngredients(
            List<UpdateMenuBurgerCommand.IngredientRequest> ingredients) {

        long uniqueProducts = ingredients.stream()
                .map(UpdateMenuBurgerCommand.IngredientRequest::idProduct)
                .distinct()
                .count();

        if (uniqueProducts < ingredients.size()) {
            throw new InvalidBurgerException(
                    "La hamburguesa contiene ingredientes duplicados. " +
                            "Si deseas más cantidad, aumenta el campo 'cantidad'"
            );
        }
    }

    private void validateProductForBurger(Product product) {

        if (!product.getAvailability()) {
            throw new InvalidBurgerException(
                    "El producto '" + product.getName() + "' no está disponible"
            );
        }

        if (!ALLOWED_INGREDIENT_TYPES.contains(product.getProductType())) {
            throw new InvalidBurgerException(
                    "El producto '" + product.getName() + "' debe ser de tipo INGREDIENT. " +
                            "Tipo actual: " + product.getProductType()
            );
        }

        if (product.getQuantity() <= 0) {
            throw new InsufficientStockException(
                    "El producto '" + product.getName() + "' no tiene stock disponible"
            );
        }
    }
}