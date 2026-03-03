package com.tetris.tetrisburger_backend.application.usecase.burger.admin;

import com.tetris.tetrisburger_backend.domain.exception.BurgerCreationException;
import com.tetris.tetrisburger_backend.domain.exception.BurgerNotFoundException;
import com.tetris.tetrisburger_backend.domain.exception.InsufficientStockException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidBurgerException;
import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.BurgerIngredient;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.model.ProductType;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.UpdateMenuBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateMenuBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UpdateMenuBurgerUseCase implements UpdateMenuBurger {

    private static final Logger logger = LoggerFactory.getLogger(UpdateMenuBurgerUseCase.class);

    // Tipos de productos permitidos para ingredientes de hamburguesa
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
        logger.info("🔵 Actualizando hamburguesa de menú: idBurger={}, updatedBy={}",
                command.idBurger(), command.updatedBy());

        try {
            // 1. Validaciones
            validateCommand(command);

            // 2. Buscar burger de menú
            Burger burger = burgerRepository.findById(command.idBurger())
                    .orElseThrow(() -> new BurgerNotFoundException(
                            "Hamburguesa no encontrada. ID: " + command.idBurger()
                    ));

            // 3. Validar que sea burger de menú activa
            validateIsMenuBurger(burger);

            logger.debug(" Estado actual: name={}, ingredients={}, basePrice={}, finalPrice={}",
                    burger.getName(),
                    burger.getIngredients().size(),
                    burger.getBasePrice(),
                    burger.getFinalPrice());

            // 4. Validar ingredientes duplicados
            validateNoDuplicateIngredients(command.ingredients());

            // 5.  Crear snapshots de productos (igual que en Create)
            List<BurgerIngredient> newIngredients = command.ingredients().stream()
                    .map(req -> {
                        Product product = productRepository.findById(req.idProduct())
                                .orElseThrow(() -> new ProductNotFoundException(req.idProduct()));

                        // Validación completa del producto
                        validateProductForBurger(product);

                        //  Crear snapshot del producto
                        ProductSnapshot snapshot = ProductSnapshot.fromProduct(
                                product,
                                req.quantity()

                        );

                        // Crear ingrediente desde snapshot
                        return BurgerIngredient.fromSnapshot(snapshot);
                    })
                    .toList();

            // 6. Guardar precios actuales para logging
            BigDecimal oldBasePrice = burger.getBasePrice();
            BigDecimal oldFinalPrice = burger.getFinalPrice();

            // 7. Delegar la actualización al dominio
            burger.updateMenuBurger(
                    command.name(),
                    command.description(),
                    newIngredients,
                    command.availability(),
                    command.updatedBy()

            );

            // 8. Logging de cambios
            logger.info(" Actualización de precios:");
            logger.info("  • Base:  ${} → ${}", oldBasePrice, burger.getBasePrice());
            logger.info("  • Final: ${} → ${}", oldFinalPrice, burger.getFinalPrice());

            if (!burger.getFinalPrice().equals(burger.getBasePrice())) {
                logger.info("  • Margen personalizado mantenido: {}%",
                        burger.calculateMarginPercentage());
            }

            logger.debug(" Estado actualizado: name={}, ingredients={}, basePrice={}, finalPrice={}",
                    burger.getName(),
                    burger.getIngredients().size(),
                    burger.getBasePrice(),
                    burger.getFinalPrice());

            // 9. Guardar
            Burger updated = burgerRepository.save(burger);

            if (updated == null) {
                throw new BurgerCreationException("Error al guardar la hamburguesa de menú actualizada");
            }

            logger.info(" Hamburguesa de menú actualizada exitosamente: burgerId={}", updated.getIdBurger());

            return updated;

        } catch (BurgerNotFoundException | ProductNotFoundException |
                 InvalidBurgerException | InsufficientStockException e) {
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidBurgerException(e.getMessage());
        } catch (Exception e) {
            logger.error(" Error inesperado actualizando hamburguesa de menú: burgerId={}",
                    command.idBurger(), e);
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

    /**
     * Valida que sea una hamburguesa de menú activa
     */
    private void validateIsMenuBurger(Burger burger) {
        if (!burger.isOnMenu()) {
            throw new InvalidBurgerException(
                    "La hamburguesa no es una hamburguesa de menú. ID: " + burger.getIdBurger()
            );
        }

        if (burger.isCustomBurger()) {
            throw new InvalidBurgerException(
                    "No se puede actualizar una hamburguesa personalizada con este método. " +
                            "ID: " + burger.getIdBurger()
            );
        }

        if (burger.isDeleted()) {
            throw new InvalidBurgerException(
                    "No se puede actualizar una hamburguesa eliminada. ID: " + burger.getIdBurger()
            );
        }
    }

    /**
     * Valida que no haya ingredientes duplicados
     */
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

    /**
     * Validación completa del producto para hamburguesa de menú
     */
    private void validateProductForBurger(Product product) {

        // 1. Validar disponibilidad
        if (!product.getAvailability()) {
            throw new InvalidBurgerException(
                    "El producto '" + product.getName() + "' no está disponible"
            );
        }

        // 2. Validar que sea ingrediente de hamburguesa
        if (product.getIsBurgerIngredient() == null || !product.getIsBurgerIngredient()) {
            throw new InvalidBurgerException(
                    "El producto '" + product.getName() + "' no es un ingrediente de hamburguesa"
            );
        }

        // 3. Validar tipo de producto
        if (!ALLOWED_INGREDIENT_TYPES.contains(product.getProductType())) {
            throw new InvalidBurgerException(
                    "El producto '" + product.getName() + "' debe ser de tipo INGREDIENT. " +
                            "Tipo actual: " + product.getProductType()
            );
        }

        // 4. Validar stock disponible (advertencia, no bloquea para menu burgers)
        if (product.getQuantity() <= 0) {
            logger.warn("Producto sin stock usado en menu burger: {} (ID: {})",
                    product.getName(), product.getId());
        }

        logger.debug(" Producto validado: {} | Categoría: {} | Stock: {} | Tipo: {}",
                product.getName(),
                product.getCategoryName(),
                product.getQuantity(),
                product.getProductType());
    }
}
