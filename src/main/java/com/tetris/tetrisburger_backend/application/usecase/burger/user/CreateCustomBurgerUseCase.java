package com.tetris.tetrisburger_backend.application.usecase.burger.user;

import com.tetris.tetrisburger_backend.domain.exception.BurgerCreationException;
import com.tetris.tetrisburger_backend.domain.exception.InsufficientStockException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidBurgerException;
import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.BurgerSettings;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.model.ProductType;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.GetBurgerSettings;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;
import com.tetris.tetrisburger_backend.domain.port.in.burger.user.CreateCustomBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.CreateCustomBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Service
@Transactional
public class CreateCustomBurgerUseCase implements CreateCustomBurger {

    private static final Logger logger = LoggerFactory.getLogger(CreateCustomBurgerUseCase.class);

    private static final Set<ProductType> ALLOWED_INGREDIENT_TYPES = Set.of(
            ProductType.INGREDIENT
    );

    private final BurgerRepository burgerRepository;
    private final ProductRepository productRepository;
    private final GetBurgerSettings getBurgerSettings;

    public CreateCustomBurgerUseCase(
            BurgerRepository burgerRepository,
            ProductRepository productRepository,
            GetBurgerSettings getBurgerSettings
    ) {
        this.burgerRepository = burgerRepository;
        this.productRepository = productRepository;
        this.getBurgerSettings = getBurgerSettings;
    }

    @Override
    public Burger handle(CreateCustomBurgerCommand command) {
        logger.info("Creando hamburguesa personalizada: name={}, userId={}",
                command.name(), command.createdBy());

        try {
            //  0. Obtener y validar settings PRIMERO
            BurgerSettings settings = getBurgerSettings.handle();
            validateCustomBurgersEnabled(settings);

            // 1. Validar command
            validateCommand(command);

            //  2. Validar cantidad de ingredientes contra settings
            validateIngredientCount(command.ingredients().size(), settings);

            // 3. Validar ingredientes duplicados
            validateNoDuplicateIngredients(command.ingredients());

            // 4. Iniciar el builder
            Burger.CustomBuilder builder = new Burger.CustomBuilder(
                    command.name(),
                    command.createdBy()
            );

            // 5. Agregar descripción si existe
            if (command.description() != null && !command.description().isBlank()) {
                builder.withDescription(command.description());
            }

            // 6. Agregar ingredientes uno por uno
            for (CreateCustomBurgerCommand.IngredientRequest ing : command.ingredients()) {
                // Buscar el producto
                Product product = productRepository.findById(ing.idProduct())
                        .orElseThrow(() -> new ProductNotFoundException(ing.idProduct()));

                // Validación completa del producto
                validateProductForBurger(product, ing.quantity());

                // Crear snapshot del producto
                ProductSnapshot snapshot = ProductSnapshot.fromProduct(
                        product,
                        ing.quantity(),
                        ing.isOptional()
                );

                // Agregar al builder (calcula el precio automáticamente)
                builder.addIngredient(snapshot);
            }

            // 7. Construir la burger
            Burger burger = builder.build();

            //  8. Validar precio final contra settings
            validateBurgerPrice(burger.getFinalPrice(), settings);

            logger.info(" Guardando hamburguesa personalizada: name={}, userId={}, price={}, ingredients={}",
                    burger.getName(), burger.getIdUser(), burger.getFinalPrice(),
                    burger.getIngredients().size());

            // 9. Guardar en repositorio
            Burger savedBurger = burgerRepository.save(burger);

            if (savedBurger == null || savedBurger.getIdBurger() == null) {
                throw new BurgerCreationException("Error al guardar la hamburguesa en la base de datos");
            }

            logger.info(" Hamburguesa personalizada guardada exitosamente: ID={}",
                    savedBurger.getIdBurger());

            return savedBurger;

        } catch (ProductNotFoundException | InvalidBurgerException |
                 InsufficientStockException | BurgerCreationException e) {
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidBurgerException(e.getMessage());
        } catch (Exception e) {
            logger.error(" Error inesperado creando hamburguesa personalizada", e);
            throw new BurgerCreationException("Error creando hamburguesa personalizada", e);
        }
    }

    // ==================== VALIDACIONES DE SETTINGS ==================== ✅ NUEVO

    /**
     * ✅ Valida que las hamburguesas personalizadas estén habilitadas
     */
    private void validateCustomBurgersEnabled(BurgerSettings settings) {
        if (!settings.isCustomBurgersEnabled()) {
            logger.warn(" Intento de crear burger personalizada con feature deshabilitada");
            throw new InvalidBurgerException(
                    "Las hamburguesas personalizadas están temporalmente deshabilitadas. " +
                            "Por favor, intenta más tarde o elige una de nuestras hamburguesas del menú."
            );
        }
    }

    /**
     * ✅ Valida que la cantidad de ingredientes esté dentro de los límites configurados
     */
    private void validateIngredientCount(int ingredientCount, BurgerSettings settings) {
        if (ingredientCount < settings.getMinIngredients()) {
            throw new InvalidBurgerException(
                    String.format(
                            "La hamburguesa debe tener al menos %d ingrediente(s). " +
                                    "Cantidad actual: %d",
                            settings.getMinIngredients(),
                            ingredientCount
                    )
            );
        }

        if (ingredientCount > settings.getMaxIngredients()) {
            throw new InvalidBurgerException(
                    String.format(
                            "La hamburguesa no puede tener más de %d ingredientes. " +
                                    "Cantidad actual: %d",
                            settings.getMaxIngredients(),
                            ingredientCount
                    )
            );
        }

        logger.debug("✅ Cantidad de ingredientes válida: {} (Min: {}, Max: {})",
                ingredientCount, settings.getMinIngredients(), settings.getMaxIngredients());
    }

    /**
     * ✅ Valida que el precio final esté dentro del rango configurado
     */
    private void validateBurgerPrice(BigDecimal finalPrice, BurgerSettings settings) {
        if (finalPrice.compareTo(settings.getCustomBurgerMinPrice()) < 0) {
            throw new InvalidBurgerException(
                    String.format(
                            "El precio de la hamburguesa ($%,.0f) es menor al mínimo permitido ($%,.0f). " +
                                    "Agrega más ingredientes.",
                            finalPrice,
                            settings.getCustomBurgerMinPrice()
                    )
            );
        }

        if (finalPrice.compareTo(settings.getCustomBurgerMaxPrice()) > 0) {
            throw new InvalidBurgerException(
                    String.format(
                            "El precio de la hamburguesa ($%,.0f) excede el máximo permitido ($%,.0f). " +
                                    "Reduce la cantidad de ingredientes.",
                            finalPrice,
                            settings.getCustomBurgerMaxPrice()
                    )
            );
        }

        logger.debug(" Precio válido: ${} (Min: ${}, Max: ${})",
                finalPrice, settings.getCustomBurgerMinPrice(), settings.getCustomBurgerMaxPrice());
    }

    // ==================== VALIDACIONES ORIGINALES ====================

    private void validateCommand(CreateCustomBurgerCommand command) {
        if (command == null) {
            throw new InvalidBurgerException("El comando no puede ser nulo");
        }

        if (command.name() == null || command.name().isBlank()) {
            throw new InvalidBurgerException("El nombre no puede estar vacío");
        }

        if (command.createdBy() == null) {
            throw new InvalidBurgerException("El ID del usuario es obligatorio");
        }

        if (command.ingredients() == null || command.ingredients().isEmpty()) {
            throw new InvalidBurgerException(
                    "La hamburguesa debe tener al menos un ingrediente"
            );
        }
    }

    /**
     * Valida que no haya ingredientes duplicados
     */
    private void validateNoDuplicateIngredients(
            java.util.List<CreateCustomBurgerCommand.IngredientRequest> ingredients) {

        long uniqueProducts = ingredients.stream()
                .map(CreateCustomBurgerCommand.IngredientRequest::idProduct)
                .distinct()
                .count();

        if (uniqueProducts < ingredients.size()) {
            throw new InvalidBurgerException(
                    "La hamburguesa contiene ingredientes duplicados. " +
                            "Si deseas más cantidad, aumenta el campo 'quantity'"
            );
        }
    }

    /**
     * Validación completa del producto para hamburguesa personalizada
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

        // 4. Validar stock disponible
        if (product.getQuantity() <= 0) {
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

        logger.debug(" Producto validado: {} | Categoría: {} | Stock: {} | Solicitado: {}",
                product.getName(),
                product.getCategoryName(),
                product.getQuantity(),
                quantity);
    }
}
