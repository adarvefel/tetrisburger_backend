package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.exception.*;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.BurgerIngredient;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.model.ProductType;
import com.tetris.tetrisburger_backend.domain.port.in.burger.UpdateCustomBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;
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
import java.util.Set;

@Service
@Transactional
public class UpdateCustomBurgerUseCase implements UpdateCustomBurger {

    private static final Logger logger = LoggerFactory.getLogger(UpdateCustomBurgerUseCase.class);

    // Tipos de productos permitidos para ingredientes de hamburguesa
    private static final Set<ProductType> ALLOWED_INGREDIENT_TYPES = Set.of(
            ProductType.INGREDIENT
    );

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
        logger.info("Actualizando hamburguesa personalizada: burgerId={}, userId={}",
                command.idBurger(), command.idUser());

        try {
            // 1. Validaciones básicas
            validateCommand(command);
            validateUser(command.idUser());

            // 2. Cargar burger y validar dueño
            Burger burger = burgerRepository.findCustomByIdAndUser(
                            command.idBurger(), command.idUser())
                    .orElseThrow(() -> new BurgerNotFoundException(
                            "Hamburguesa no encontrada o no pertenece al usuario. ID: " + command.idBurger()
                    ));

            // 3. Validar que es custom burger y no está eliminada
            validateIsCustomBurger(burger);

            logger.debug("📊 Estado actual: name={}, ingredients={}, price={}",
                    burger.getName(), burger.getIngredients().size(), burger.getFinalPrice());

            // 4. Validar ingredientes duplicados
            validateNoDuplicateIngredients(command.ingredients());

            // 5. Crear nuevos ingredientes con validaciones completas
            List<BurgerIngredient> newIngredients = command.ingredients().stream()
                    .map(this::createBurgerIngredient)
                    .toList();

            // 6. Guardar precios actuales para logging
            BigDecimal oldPrice = burger.getFinalPrice();

            // 7. Delegar la actualización al dominio
            burger.updateCustomBurger(
                    command.idUser(),
                    command.name(),
                    command.description(),
                    newIngredients
            );

            logger.debug("✅ Estado actualizado: name={}, ingredients={}, price={} (antes: {})",
                    burger.getName(),
                    burger.getIngredients().size(),
                    burger.getFinalPrice(),
                    oldPrice);

            // 8. Guardar
            Burger updated = burgerRepository.save(burger);

            if (updated == null) {
                throw new BurgerCreationException("Error al guardar hamburguesa actualizada");
            }

            logger.info("✅ Hamburguesa personalizada actualizada: burgerId={}, userId={}, precio: ${} → ${}",
                    updated.getIdBurger(),
                    command.idUser(),
                    oldPrice,
                    updated.getFinalPrice());

            return updated;

        } catch (BurgerNotFoundException | UserNotFoundException |
                 ProductNotFoundException | InsufficientStockException |
                 InvalidBurgerException e) {
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidBurgerException(e.getMessage());
        } catch (Exception e) {
            logger.error("❌ Error inesperado actualizando hamburguesa: burgerId={}",
                    command.idBurger(), e);
            throw new BurgerCreationException("Error actualizando hamburguesa personalizada", e);
        }
    }

    // ==================== VALIDACIONES ====================

    private void validateCommand(UpdateCustomBurgerCommand command) {
        if (command == null) {
            throw new InvalidBurgerException("El comando no puede ser nulo");
        }

        if (command.idBurger() == null) {
            throw new InvalidBurgerException("El ID de la hamburguesa no puede ser nulo");
        }

        if (command.idUser() == null) {
            throw new InvalidBurgerException("El ID del usuario no puede ser nulo");
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

    /**
     * Valida que sea custom burger y no esté eliminada
     */
    private void validateIsCustomBurger(Burger burger) {
        if (!burger.isCustomBurger()) {
            throw new InvalidBurgerException(
                    "Solo hamburguesas personalizadas pueden actualizarse con este método. " +
                            "Burger ID: " + burger.getIdBurger()
            );
        }

        if (burger.isDeleted()) {
            throw new InvalidBurgerException(
                    "No se puede actualizar una hamburguesa eliminada. " +
                            "Burger ID: " + burger.getIdBurger()
            );
        }
    }

    /**
     * Valida que no haya ingredientes duplicados
     */
    private void validateNoDuplicateIngredients(
            List<UpdateCustomBurgerCommand.IngredientRequest> ingredients) {

        long uniqueProducts = ingredients.stream()
                .map(UpdateCustomBurgerCommand.IngredientRequest::idProduct)
                .distinct()
                .count();

        if (uniqueProducts < ingredients.size()) {
            throw new InvalidBurgerException(
                    "La hamburguesa contiene ingredientes duplicados. " +
                            "Si deseas más cantidad, aumenta el campo 'quantity'"
            );
        }
    }

    // ==================== CREAR INGREDIENTE ====================

    /**
     * Crea y valida un BurgerIngredient desde un IngredientRequest
     * ✅ Usa ProductSnapshot para capturar el estado del producto
     */
    private BurgerIngredient createBurgerIngredient(
            UpdateCustomBurgerCommand.IngredientRequest request) {

        logger.debug("🔍 Validando ingrediente: productId={}, quantity={}",
                request.idProduct(), request.quantity());

        // 1. Buscar producto
        Product product = productRepository.findById(request.idProduct())
                .orElseThrow(() -> new ProductNotFoundException(request.idProduct()));

        // 2. Validar producto completo (incluye stock)
        validateProductForBurger(product, request.quantity());

        // 3. Crear snapshot del producto
        ProductSnapshot snapshot = ProductSnapshot.fromProduct(
                product,
                request.quantity(),
                request.isOptional()
        );

        logger.debug("✓ Ingrediente válido: {} x{} = ${}",
                product.getName(),
                request.quantity(),
                snapshot.calculateSubtotal());

        // 4. Crear ingrediente desde snapshot
        return BurgerIngredient.fromSnapshot(snapshot);
    }

    /**
     * Validación completa del producto para hamburguesa personalizada
     * ⚠️ IMPORTANTE: Custom burgers SÍ requieren stock disponible
     */
    private void validateProductForBurger(Product product, Integer quantity) {

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

        // 4. ⚠️ CRÍTICO: Validar stock disponible (Custom burgers SÍ requieren stock)
        if (product.getQuantity() == null || product.getQuantity() <= 0) {
            throw new InsufficientStockException(
                    "El producto '" + product.getName() + "' no tiene stock disponible"
            );
        }

        // 5. Validar cantidad suficiente
        if (product.getQuantity() < quantity) {
            throw new InsufficientStockException(
                    "Stock insuficiente para '" + product.getName() + "'. " +
                            "Disponible: " + product.getQuantity() + ", Solicitado: " + quantity
            );
        }

        logger.debug("✅ Producto validado: {} | Categoría: {} | Stock: {} | Solicitado: {}",
                product.getName(),
                product.getCategoryName(),
                product.getQuantity(),
                quantity);
    }
}
