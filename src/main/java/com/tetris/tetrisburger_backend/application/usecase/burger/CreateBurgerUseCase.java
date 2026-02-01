package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.application.event.MenuBurgerImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.exception.BurgerCreationException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidBurgerException;
import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.Product;
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

@Service
@Transactional
public class CreateBurgerUseCase implements CreateMenuBurger {

    private static final Logger logger = LoggerFactory.getLogger(CreateBurgerUseCase.class);

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
        logger.info("Creando hamburguesa de menú: name={}, createdBy={}",
                command.name(), command.createdBy());

        try {
            // 1. Validar command
            validateCommand(command);

            // 2. ✅ VALIDAR QUE NO EXISTA DUPLICADO
            validateUniqueMenuBurgerName(command.name());

            // 3. Crear snapshots de productos
            List<ProductSnapshot> snapshots = command.ingredients().stream()
                    .map(ing -> {
                        Product product = productRepository.findById(ing.idProduct())
                                .orElseThrow(() -> new ProductNotFoundException(ing.idProduct()));

                        validateProduct(product);

                        return ProductSnapshot.fromProduct(
                                product,
                                ing.quantity(),
                                ing.isOptional()
                        );
                    })
                    .toList();

            // 4. Crear burger de menú SIN imagen (se subirá después)
            Burger burger = Burger.menuBurger(
                    command.name(),
                    command.description(),
                    null,  // Sin imageUrl inicialmente
                    snapshots,
                    command.isFavorite()
            );

            // 5. Establecer auditoría
            if (command.createdBy() != null) {
                burger.setCreatedBy(command.createdBy());
                burger.setUpdatedBy(command.createdBy());
            }

            // 6. Actualizar precio final si viene en el command
            if (command.finalPrice() != null &&
                    command.finalPrice().compareTo(BigDecimal.ZERO) > 0) {

                logger.info("Actualizando precio de hamburguesa de menú: basePrice={}, newFinalPrice={}",
                        burger.getBasePrice(), command.finalPrice());

                burger.updatePrice(command.finalPrice(), command.createdBy());
            }

            logger.info("Guardando hamburguesa de menú: name={}, basePrice={}, finalPrice={}, ingredients={}",
                    command.name(), burger.getBasePrice(), burger.getFinalPrice(),
                    burger.getIngredients().size());

            // 7. Guardar el burger
            Burger savedBurger = burgerRepository.save(burger);

            if (savedBurger == null || savedBurger.getIdBurger() == null) {
                throw new BurgerCreationException("Error al guardar la hamburguesa en la base de datos");
            }

            logger.info("Hamburguesa de menú guardada exitosamente con ID: {}", savedBurger.getIdBurger());

            // 8. Publicar evento para subir la imagen de forma asíncrona
            publishImageUploadEvent(savedBurger, command);

            return savedBurger;

        } catch (ProductNotFoundException | InvalidBurgerException | BurgerCreationException e) {
            // Propagar excepciones conocidas sin wrappear
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidBurgerException(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado creando hamburguesa de menú", e);
            throw new BurgerCreationException("Error creando hamburguesa de menú", e);
        }
    }

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
    }

    /**
     * ✅ Valida que no exista otra burger de menú activa con el mismo nombre
     *
     * @param name Nombre de la burger a validar
     * @throws BurgerCreationException si ya existe una burger con ese nombre
     */
    private void validateUniqueMenuBurgerName(String name) {
        logger.debug("Validando que no exista burger de menú con nombre: {}", name);

        if (burgerRepository.existsByNameAndIsOnMenuTrueAndDeletedAtIsNull(name)) {
            logger.warn("Intento de crear burger de menú con nombre duplicado: {}", name);
            throw new BurgerCreationException(
                    "Ya existe una hamburguesa de menú con el nombre: '" + name + "'"
            );
        }

        logger.debug("Validación de nombre único exitosa para: {}", name);
    }

    /**
     * Valida que el producto esté disponible
     */
    private void validateProduct(Product product) {
        if (!product.isAvailable()) {
            throw new InvalidBurgerException(
                    "El producto '" + product.getName() + "' no está disponible"
            );
        }
    }

    /**
     * Publica evento para subir imagen de forma asíncrona
     */
    private void publishImageUploadEvent(Burger burger, CreateBurgerCommand command) {
        if (command.imageData() != null && command.imageData().bytes() != null) {
            logger.info("Publicando evento MenuBurgerImageUploadRequestedEvent para burgerId: {}",
                    burger.getIdBurger());

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
            logger.debug("No hay imagen para subir en burger ID: {}", burger.getIdBurger());
        }
    }
}
