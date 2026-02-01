package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.application.event.CustomBurgerImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.exception.BurgerCreationException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidBurgerException;
import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;
import com.tetris.tetrisburger_backend.domain.port.in.burger.CreateCustomBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.CreateCustomBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CreateCustomBurgerUseCase implements CreateCustomBurger {

    private static final Logger logger = LoggerFactory.getLogger(CreateCustomBurgerUseCase.class);

    private final BurgerRepository burgerRepository;
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CreateCustomBurgerUseCase(BurgerRepository burgerRepository,
                                     ProductRepository productRepository,
                                     ApplicationEventPublisher eventPublisher) {
        this.burgerRepository = burgerRepository;
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Burger handle(CreateCustomBurgerCommand command) {
        logger.info("Creando hamburguesa personalizada: name={}, userId={}",
                command.name(), command.createdBy());

        try {
            // 1. Validar command
            validateCommand(command);

            // 2. Iniciar el builder
            Burger.CustomBuilder builder = new Burger.CustomBuilder(
                    command.name(),
                    command.createdBy()
            );

            // 3. Agregar descripción si existe
            if (command.description() != null && !command.description().isBlank()) {
                builder.withDescription(command.description());
            }

            // 4. Agregar ingredientes uno por uno
            for (CreateCustomBurgerCommand.IngredientRequest ing : command.ingredients()) {
                // Buscar el producto
                Product product = productRepository.findById(ing.idProduct())
                        .orElseThrow(() -> new ProductNotFoundException(ing.idProduct()));

                // Validar que esté disponible
                validateProduct(product);

                // Crear snapshot del producto
                ProductSnapshot snapshot = ProductSnapshot.fromProduct(
                        product,
                        ing.quantity(),
                        ing.isOptional()
                );

                // Agregar al builder (el builder calcula el precio automáticamente)
                builder.addIngredient(snapshot);
            }

            // 5. Construir la burger
            Burger burger = builder.build();

            logger.info("Guardando hamburguesa personalizada: name={}, userId={}, finalPrice={}, ingredients={}",
                    burger.getName(), burger.getIdUser(), burger.getFinalPrice(),
                    burger.getIngredients().size());

            // 6. Guardar en repositorio
            Burger savedBurger = burgerRepository.save(burger);

            if (savedBurger == null || savedBurger.getIdBurger() == null) {
                throw new BurgerCreationException("Error al guardar la hamburguesa en la base de datos");
            }

            logger.info("Hamburguesa personalizada guardada exitosamente con ID: {}",
                    savedBurger.getIdBurger());

            // 7. Publicar evento para subir imagen si existe
            if (command.imageData() != null && command.imageData().bytes() != null) {
                publishImageUploadEvent(savedBurger, command);
            }

            return savedBurger;

        } catch (ProductNotFoundException | InvalidBurgerException e) {
            logger.error("Error de validación creando hamburguesa personalizada: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.error("Error de lógica de negocio: {}", e.getMessage());
            throw new InvalidBurgerException(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado creando hamburguesa personalizada", e);
            throw new BurgerCreationException("Error creando hamburguesa personalizada", e);
        }
    }

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

    private void validateProduct(Product product) {
        if (!product.getAvailability()) {
            throw new InvalidBurgerException(
                    "El producto '" + product.getName() + "' no está disponible"
            );
        }

        // Opcional: Validar stock si es necesario
        if (product.getQuantity() != null && product.getQuantity() <= 0) {
            throw new InvalidBurgerException(
                    "El producto '" + product.getName() + "' no tiene stock disponible"
            );
        }
    }

    private void publishImageUploadEvent(Burger burger, CreateCustomBurgerCommand command) {
        logger.info("Publicando evento CustomBurgerImageUploadRequestedEvent para burgerId: {}",
                burger.getIdBurger());

        eventPublisher.publishEvent(
                new CustomBurgerImageUploadRequestedEvent(
                        burger.getIdBurger(),
                        command.createdBy(),
                        command.imageData().bytes(),
                        command.imageData().contentType(),
                        command.imageData().originalFilename()
                )
        );
    }
}
