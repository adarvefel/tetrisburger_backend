package com.tetris.tetrisburger_backend.application.usecase.burger.user;

import com.tetris.tetrisburger_backend.domain.exception.BurgerCreationException;
import com.tetris.tetrisburger_backend.domain.exception.InsufficientStockException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidBurgerException;
import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.model.ProductType;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;
import com.tetris.tetrisburger_backend.domain.port.in.burger.user.CreateCustomBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.CreateCustomBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

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

    public CreateCustomBurgerUseCase(BurgerRepository burgerRepository,
                                     ProductRepository productRepository
                                     ) {
        this.burgerRepository = burgerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Burger handle(CreateCustomBurgerCommand command) {
        logger.info("Creando hamburguesa personalizada: name={}, userId={}",
                command.name(), command.createdBy());

        try {
            // 1. Validar command
            validateCommand(command);

            // 2. Validar ingredientes duplicados
            validateNoDuplicateIngredients(command.ingredients());

            // 3. Iniciar el builder
            Burger.CustomBuilder builder = new Burger.CustomBuilder(
                    command.name(),
                    command.createdBy()
            );

            // 4. Agregar descripción si existe
            if (command.description() != null && !command.description().isBlank()) {
                builder.withDescription(command.description());
            }

            // 5. Agregar ingredientes uno por uno
            for (CreateCustomBurgerCommand.IngredientRequest ing : command.ingredients()) {
                // Buscar el producto
                Product product = productRepository.findById(ing.idProduct())
                        .orElseThrow(() -> new ProductNotFoundException(ing.idProduct()));

                //  Validación completa del producto
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

            // 6. Construir la burger
            Burger burger = builder.build();

            logger.info(" Guardando hamburguesa personalizada: name={}, userId={}, price={}, ingredients={}",
                    burger.getName(), burger.getIdUser(), burger.getFinalPrice(),
                    burger.getIngredients().size());

            // 7. Guardar en repositorio
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

    // ==================== VALIDACIONES ====================

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
     *  Valida que no haya ingredientes duplicados
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
     *  Validación completa del producto para hamburguesa personalizada
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
