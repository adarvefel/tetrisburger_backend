package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.application.event.MenuBurgerImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.exception.BurgerCreationException;
import com.tetris.tetrisburger_backend.domain.exception.InsufficientStockException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidBurgerException;
import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.model.ProductType;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;
import com.tetris.tetrisburger_backend.domain.port.in.burger.CreateMenuBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.CreateBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class CreateBurgerUseCase implements CreateMenuBurger {

    private static final Logger logger = LoggerFactory.getLogger(CreateBurgerUseCase.class);

    // Tipos de productos permitidos para ingredientes de hamburguesa
    private static final Set<ProductType> ALLOWED_INGREDIENT_TYPES = Set.of(
            ProductType.INGREDIENT
    );

    private final BurgerRepository burgerRepository;
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CreateBurgerUseCase(BurgerRepository burgerRepository,
                               ProductRepository productRepository,
                               ApplicationEventPublisher eventPublisher) {
        this.burgerRepository = burgerRepository;
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Burger handle(CreateBurgerCommand command) {
        logger.info("🔵 Creando hamburguesa de menú: name={}, createdBy={}",
                command.name(), command.createdBy());

        try {
            // 1. Validar command
            validateCommand(command);

            // 2. Validar que no exista duplicado
            validateUniqueMenuBurgerName(command.name());

            // 3. Validar ingredientes duplicados
            validateNoDuplicateIngredients(command.ingredients());

            // 4. Crear snapshots de productos con validaciones completas
            List<ProductSnapshot> snapshots = command.ingredients().stream()
                    .map(ing -> {
                        Product product = productRepository.findById(ing.idProduct())
                                .orElseThrow(() -> new ProductNotFoundException(ing.idProduct()));

                        // Validación completa del producto
                        validateProductForBurger(product);

                        return ProductSnapshot.fromProduct(
                                product,
                                ing.quantity(),
                                ing.isOptional()
                        );
                    })
                    .toList();

            // 5. Crear burger de menú SIN imagen (se subirá después)
            Burger burger = Burger.menuBurger(
                    command.name(),
                    command.description(),
                    null,
                    snapshots,
                    command.isFavorite() != null ? command.isFavorite() : false
            );

            // 6. Establecer auditoría
            if (command.createdBy() != null) {
                burger.setCreatedBy(command.createdBy());
                burger.setUpdatedBy(command.createdBy());
            }

            // 7. ✅ Actualizar SOLO finalPrice si viene en el command (precio custom)
            if (command.finalPrice() != null &&
                    command.finalPrice().compareTo(BigDecimal.ZERO) > 0) {

                BigDecimal basePrice = burger.getBasePrice();
                BigDecimal customPrice = command.finalPrice();

                // Validar que el precio final sea razonable
                validateFinalPrice(customPrice, basePrice);

                // Calcular margen para logging
                BigDecimal margin = customPrice.subtract(basePrice);
                BigDecimal marginPercent = basePrice.compareTo(BigDecimal.ZERO) > 0
                        ? margin.divide(basePrice, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        : BigDecimal.ZERO;

                logger.info("💰 Aplicando precio personalizado:");
                logger.info("  • Costo base (ingredientes): ${}", basePrice);
                logger.info("  • Precio de venta (custom): ${}", customPrice);
                logger.info("  • Margen: ${} ({}%)", margin, marginPercent);

                // ✅ Solo actualizar finalPrice (usar setter o método de dominio)
                burger.setFinalPrice(customPrice);

            } else {
                logger.info("💰 Usando precio calculado automáticamente: ${}",
                        burger.getFinalPrice());
            }

            logger.info("📦 Guardando hamburguesa: name={}, basePrice={}, finalPrice={}, ingredients={}",
                    command.name(), burger.getBasePrice(), burger.getFinalPrice(),
                    burger.getIngredients().size());

            // 8. Guardar el burger
            Burger savedBurger = burgerRepository.save(burger);

            if (savedBurger == null || savedBurger.getIdBurger() == null) {
                throw new BurgerCreationException("Error al guardar la hamburguesa en la base de datos");
            }

            logger.info("✅ Hamburguesa de menú guardada exitosamente: ID={}, basePrice=${}, finalPrice=${}",
                    savedBurger.getIdBurger(),
                    savedBurger.getBasePrice(),
                    savedBurger.getFinalPrice());

            // 9. Publicar evento para subir la imagen de forma asíncrona
            publishImageUploadEvent(savedBurger, command);

            return savedBurger;

        } catch (ProductNotFoundException | InvalidBurgerException |
                 BurgerCreationException | InsufficientStockException e) {
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidBurgerException(e.getMessage());
        } catch (Exception e) {
            logger.error("❌ Error inesperado creando hamburguesa de menú", e);
            throw new BurgerCreationException("Error creando hamburguesa de menú", e);
        }
    }

    // ==================== VALIDACIONES ====================

    /**
     * Valida el command recibido
     */
    private void validateCommand(CreateBurgerCommand command) {
        if (command == null) {
            throw new InvalidBurgerException("El comando no puede ser nulo");
        }

        if (command.name() == null || command.name().isBlank()) {
            throw new InvalidBurgerException("El nombre no puede estar vacío");
        }

        if (command.ingredients() == null || command.ingredients().isEmpty()) {
            throw new InvalidBurgerException(
                    "La hamburguesa debe tener al menos un ingrediente"
            );
        }

        if (command.createdBy() == null) {
            throw new InvalidBurgerException("El ID del usuario creador es obligatorio");
        }
    }

    /**
     * Valida que no exista otra burger de menú activa con el mismo nombre
     */
    private void validateUniqueMenuBurgerName(String name) {
        logger.debug("🔍 Validando nombre único: {}", name);

        if (burgerRepository.existsByNameAndIsOnMenuTrueAndDeletedAtIsNull(name)) {
            logger.warn("⚠️ Intento de crear burger con nombre duplicado: {}", name);
            throw new BurgerCreationException(
                    "Ya existe una hamburguesa de menú con el nombre: '" + name + "'"
            );
        }

        logger.debug("✓ Validación de nombre único exitosa");
    }

    /**
     * Valida que no haya ingredientes duplicados
     */
    private void validateNoDuplicateIngredients(
            List<CreateBurgerCommand.IngredientRequest> ingredients) {

        long uniqueProducts = ingredients.stream()
                .map(CreateBurgerCommand.IngredientRequest::idProduct)
                .distinct()
                .count();

        if (uniqueProducts < ingredients.size()) {
            throw new InvalidBurgerException(
                    "La hamburguesa contiene ingredientes duplicados"
            );
        }
    }

    /**
     * Validación completa del producto para hamburguesa
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
        if (product.getQuantity() != null && product.getQuantity() <= 0) {
            logger.warn("⚠️ Producto sin stock usado en menu burger: {} (ID: {})",
                    product.getName(), product.getId());
        }

        logger.debug("✅ Producto validado: {} | Categoría: {} | Stock: {}",
                product.getName(),
                product.getCategoryName(),
                product.getQuantity());
    }

    /**
     * Valida que el precio final sea razonable respecto al precio base
     */
    private void validateFinalPrice(BigDecimal finalPrice, BigDecimal basePrice) {
        if (finalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidBurgerException(
                    "El precio final debe ser mayor a cero"
            );
        }

        if (basePrice.compareTo(BigDecimal.ZERO) == 0) {
            logger.warn("⚠️ Precio base es cero, no se puede validar margen");
            return;
        }

        // Calcular margen porcentual
        BigDecimal marginPercent = finalPrice.subtract(basePrice)
                .divide(basePrice, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        // Validar rangos: desde -50% (promoción) hasta +300% (premium)
        if (marginPercent.compareTo(BigDecimal.valueOf(-50)) < 0) {
            throw new InvalidBurgerException(
                    String.format("El precio no puede ser menor al 50%% del costo base. " +
                                    "Costo: $%s, Precio propuesto: $%s (descuento: %.1f%%)",
                            basePrice, finalPrice, marginPercent.abs().doubleValue())
            );
        }

        if (marginPercent.compareTo(BigDecimal.valueOf(300)) > 0) {
            throw new InvalidBurgerException(
                    String.format("El precio no puede ser mayor al 300%% del costo base. " +
                                    "Costo: $%s, Precio propuesto: $%s (margen: %.1f%%)",
                            basePrice, finalPrice, marginPercent.doubleValue())
            );
        }

        // Advertencia si se vende con pérdida
        if (finalPrice.compareTo(basePrice) < 0) {
            logger.warn("⚠️ ADVERTENCIA: Precio de venta menor al costo base. " +
                    "Se está vendiendo con pérdida: ${} < ${}", finalPrice, basePrice);
        }
    }

    // ==================== EVENTO DE IMAGEN ====================

    /**
     * Publica evento para subir imagen de forma asíncrona
     */
    private void publishImageUploadEvent(Burger burger, CreateBurgerCommand command) {
        if (command.imageData() != null && command.imageData().bytes() != null) {
            logger.info("📤 Publicando evento de imagen para burger ID: {}", burger.getIdBurger());

            eventPublisher.publishEvent(
                    new MenuBurgerImageUploadRequestedEvent(
                            burger.getIdBurger(),
                            command.imageData().bytes(),
                            command.imageData().contentType(),
                            command.imageData().originalFilename(),
                            command.createdBy()
                    )
            );
        } else {
            logger.debug("📝 Sin imagen para subir en burger ID: {}", burger.getIdBurger());
        }
    }
}
