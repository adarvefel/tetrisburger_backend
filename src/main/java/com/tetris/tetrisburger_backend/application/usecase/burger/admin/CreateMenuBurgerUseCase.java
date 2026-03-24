package com.tetris.tetrisburger_backend.application.usecase.burger.admin;

import com.tetris.tetrisburger_backend.application.event.MenuBurgerImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.exception.BurgerCreationException;
import com.tetris.tetrisburger_backend.domain.exception.InsufficientStockException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidBurgerException;
import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.enums.ProductType;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.CreateMenuBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.CreateBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class CreateMenuBurgerUseCase implements CreateMenuBurger {

    private static final Set<ProductType> ALLOWED_INGREDIENT_TYPES = Set.of(
            ProductType.INGREDIENT
    );

    private final BurgerRepository burgerRepository;
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CreateMenuBurgerUseCase(BurgerRepository burgerRepository,
                                   ProductRepository productRepository,
                                   ApplicationEventPublisher eventPublisher) {
        this.burgerRepository = burgerRepository;
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Burger handle(CreateBurgerCommand command) {
        try {
            validateCommand(command);
            validateUniqueMenuBurgerName(command.name());
            validateNoDuplicateIngredients(command.ingredients());

            List<ProductSnapshot> snapshots = command.ingredients().stream()
                    .map(ing -> {
                        Product product = productRepository.findById(ing.idProduct())
                                .orElseThrow(() -> new ProductNotFoundException(ing.idProduct()));
                        validateProductForBurger(product);
                        return ProductSnapshot.fromProduct(
                                product,
                                ing.quantity(),
                                false
                        );
                    })
                    .toList();

            Burger burger = Burger.menuBurger(
                    command.name(),
                    command.description(),
                    null,
                    snapshots,
                    command.isFeatured() != null ? command.isFeatured() : false,
                    command.availability() != null ? command.availability() : true
            );

            burger.setCreatedBy(command.createdBy());
            burger.setUpdatedBy(command.createdBy());

            if (command.finalPrice() != null &&
                    command.finalPrice().compareTo(BigDecimal.ZERO) > 0) {
                validateFinalPrice(command.finalPrice(), burger.getBasePrice());
                burger.updateFinalPrice(command.finalPrice(), command.createdBy());
            }

            Burger savedBurger = burgerRepository.save(burger);

            if (savedBurger == null || savedBurger.getIdBurger() == null) {
                throw new BurgerCreationException("Error al guardar la hamburguesa en la base de datos");
            }

            publishImageUploadEvent(savedBurger, command);

            return savedBurger;

        } catch (ProductNotFoundException | InvalidBurgerException |
                 BurgerCreationException | InsufficientStockException e) {
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidBurgerException(e.getMessage());
        } catch (Exception e) {
            throw new BurgerCreationException("Error creando hamburguesa de menú", e);
        }
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private void validateCommand(CreateBurgerCommand command) {
        if (command == null)
            throw new InvalidBurgerException("El comando no puede ser nulo");
        if (command.name() == null || command.name().isBlank())
            throw new InvalidBurgerException("El nombre no puede estar vacío");
        if (command.ingredients() == null || command.ingredients().isEmpty())
            throw new InvalidBurgerException("La hamburguesa debe tener al menos un ingrediente");
        if (command.createdBy() == null)
            throw new InvalidBurgerException("El ID del usuario creador es obligatorio");
    }

    private void validateUniqueMenuBurgerName(String name) {
        if (burgerRepository.existsByNameAndIsOnMenuTrueAndDeletedAtIsNull(name)) {
            throw new BurgerCreationException(
                    "Ya existe una hamburguesa de menú con el nombre: '" + name + "'"
            );
        }
    }

    private void validateNoDuplicateIngredients(
            List<CreateBurgerCommand.IngredientRequest> ingredients) {

        long uniqueProducts = ingredients.stream()
                .map(CreateBurgerCommand.IngredientRequest::idProduct)
                .distinct()
                .count();

        if (uniqueProducts < ingredients.size()) {
            throw new InvalidBurgerException("La hamburguesa contiene ingredientes duplicados");
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
    }

    private void validateFinalPrice(BigDecimal finalPrice, BigDecimal basePrice) {
        if (finalPrice.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidBurgerException("El precio final debe ser mayor a cero");

        if (basePrice.compareTo(BigDecimal.ZERO) == 0) return;

        BigDecimal marginPercent = finalPrice.subtract(basePrice)
                .divide(basePrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

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
    }

    private void publishImageUploadEvent(Burger burger, CreateBurgerCommand command) {
        if (command.imageData() != null && command.imageData().bytes() != null) {
            eventPublisher.publishEvent(
                    new MenuBurgerImageUploadRequestedEvent(
                            burger.getIdBurger(),
                            command.imageData().bytes(),
                            command.imageData().contentType(),
                            command.imageData().originalFilename(),
                            command.createdBy()
                    )
            );
        }
    }
}
