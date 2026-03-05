//package com.tetris.tetrisburger_backend.application.usecase.burger.user;
//
//import com.tetris.tetrisburger_backend.domain.exception.*;
//import com.tetris.tetrisburger_backend.domain.model.Burger;
//import com.tetris.tetrisburger_backend.domain.model.BurgerIngredient;
//import com.tetris.tetrisburger_backend.domain.model.BurgerSettings;
//import com.tetris.tetrisburger_backend.domain.model.Product;
//import com.tetris.tetrisburger_backend.domain.model.ProductType;
//import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.GetBurgerSettings;
//import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.UpdateCustomBurger;
//import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;
//import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateCustomBurgerCommand;
//import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
//import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
//import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
//import jakarta.transaction.Transactional;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Set;
//
//@Service
//@Transactional
//public class UpdateCustomBurgerUseCase implements UpdateCustomBurger {
//
//    private static final Logger logger = LoggerFactory.getLogger(UpdateCustomBurgerUseCase.class);
//
//    private static final Set<ProductType> ALLOWED_INGREDIENT_TYPES = Set.of(
//            ProductType.INGREDIENT
//    );
//
//    private final BurgerRepository burgerRepository;
//    private final ProductRepository productRepository;
//    private final UserRepository userRepository;
//    private final GetBurgerSettings getBurgerSettings;
//
//    public UpdateCustomBurgerUseCase(
//            BurgerRepository burgerRepository,
//            ProductRepository productRepository,
//            UserRepository userRepository,
//            GetBurgerSettings getBurgerSettings
//    ) {
//        this.burgerRepository = burgerRepository;
//        this.productRepository = productRepository;
//        this.userRepository = userRepository;
//        this.getBurgerSettings = getBurgerSettings;
//    }
//
//    @Override
//    public Burger handle(UpdateCustomBurgerCommand command) {
//        logger.info("🔄 Actualizando hamburguesa personalizada: burgerId={}, userId={}",
//                command.idBurger(), command.idUser());
//
//        try {
//            // ✅ 1. Validar feature enabled
//            BurgerSettings settings = getBurgerSettings.handle();
//            validateCustomBurgersEnabled(settings);
//
//            // 2. Validaciones básicas
//            validateCommand(command);
//            validateUser(command.idUser());
//
//            // 3. Cargar burger existente
//            Burger burger = burgerRepository.findCustomByIdAndUser(
//                            command.idBurger(), command.idUser())
//                    .orElseThrow(() -> new BurgerNotFoundException(
//                            "Hamburguesa no encontrada o no pertenece al usuario. ID: " + command.idBurger()
//                    ));
//
//            validateIsCustomBurger(burger);
//
//            logger.debug("📊 Estado actual: name={}, ingredients={}, price={}",
//                    burger.getName(), burger.getIngredients().size(), burger.getFinalPrice());
//
//            // ✅ 4. DETECTAR si está modificando ingredientes
//            List<Integer> currentIngredientIds = burger.getIngredients().stream()
//                    .map(BurgerIngredient::getIdProduct)
//                    .toList();
//
//            boolean isModifyingIngredients = isModifyingIngredients(
//                    command.ingredients(),
//                    currentIngredientIds
//            );
//
//            logger.debug("📝 Modificando ingredientes: {}", isModifyingIngredients);
//
//            // ✅ 5. SI modifica ingredientes → VALIDAR contra settings
//            if (isModifyingIngredients) {
//                logger.info("⚠️ Usuario está modificando ingredientes, validando contra settings");
//                validateIngredientCount(command.ingredients().size(), settings);
//            } else {
//                logger.info("✅ Solo actualiza nombre/descripción, sin validación de ingredientes");
//            }
//
//            // 6. Validar ingredientes duplicados
//            validateNoDuplicateIngredients(command.ingredients());
//
//            // 7. Crear nuevos ingredientes con validaciones
//            List<BurgerIngredient> newIngredients = command.ingredients().stream()
//                    .map(this::createBurgerIngredient)
//                    .toList();
//
//            BigDecimal oldPrice = burger.getFinalPrice();
//
//            // 8. Actualizar burger
//            burger.updateCustomBurger(
//                    command.idUser(),
//                    command.name(),
//                    command.description(),
//                    newIngredients
//            );
//
//            // ✅ 9. SI modifica ingredientes → VALIDAR precio final
//            if (isModifyingIngredients) {
//                validateBurgerPrice(burger.getFinalPrice(), settings);
//            }
//
//            logger.debug("✅ Estado actualizado: name={}, ingredients={}, price={} (antes: {})",
//                    burger.getName(),
//                    burger.getIngredients().size(),
//                    burger.getFinalPrice(),
//                    oldPrice);
//
//            // 10. Guardar
//            Burger updated = burgerRepository.save(burger);
//
//            if (updated == null) {
//                throw new BurgerCreationException("Error al guardar hamburguesa actualizada");
//            }
//
//            logger.info("✅ Hamburguesa actualizada: burgerId={}, userId={}, precio: ${} → ${}",
//                    updated.getIdBurger(),
//                    command.idUser(),
//                    oldPrice,
//                    updated.getFinalPrice());
//
//            return updated;
//
//        } catch (BurgerNotFoundException | UserNotFoundException |
//                 ProductNotFoundException | InsufficientStockException |
//                 InvalidBurgerException e) {
//            throw e;
//        } catch (IllegalArgumentException | IllegalStateException e) {
//            throw new InvalidBurgerException(e.getMessage());
//        } catch (Exception e) {
//            logger.error("❌ Error inesperado actualizando hamburguesa: burgerId={}",
//                    command.idBurger(), e);
//            throw new BurgerCreationException("Error actualizando hamburguesa personalizada", e);
//        }
//    }
//
//    // ==================== VALIDACIONES DE SETTINGS ====================
//
//    /**
//     * ✅ Valida que custom burgers estén habilitadas
//     */
//    private void validateCustomBurgersEnabled(BurgerSettings settings) {
//        if (!settings.isCustomBurgersEnabled()) {
//            logger.warn("⚠️ Intento de actualizar burger con feature deshabilitada");
//            throw new InvalidBurgerException(
//                    "Las hamburguesas personalizadas están temporalmente deshabilitadas. " +
//                            "No se pueden realizar modificaciones en este momento."
//            );
//        }
//    }
//
//    /**
//     * ✅ Valida cantidad de ingredientes (solo si está modificando ingredientes)
//     */
//    private void validateIngredientCount(int ingredientCount, BurgerSettings settings) {
//        if (ingredientCount < settings.getMinIngredients()) {
//            throw new InvalidBurgerException(
//                    String.format(
//                            "Para modificar los ingredientes, tu hamburguesa debe cumplir los requisitos actuales: " +
//                                    "mínimo %d ingredientes. " +
//                                    "Estás intentando guardar con %d. " +
//                                    "Agrega al menos %d ingrediente(s) más.",
//                            settings.getMinIngredients(),
//                            ingredientCount,
//                            settings.getMinIngredients() - ingredientCount
//                    )
//            );
//        }
//
//        if (ingredientCount > settings.getMaxIngredients()) {
//            throw new InvalidBurgerException(
//                    String.format(
//                            "Para modificar los ingredientes, tu hamburguesa no puede exceder %d ingredientes. " +
//                                    "Estás intentando guardar con %d. " +
//                                    "Reduce al menos %d ingrediente(s).",
//                            settings.getMaxIngredients(),
//                            ingredientCount,
//                            ingredientCount - settings.getMaxIngredients()
//                    )
//            );
//        }
//
//        logger.debug("✅ Cantidad de ingredientes válida: {} (Min: {}, Max: {})",
//                ingredientCount, settings.getMinIngredients(), settings.getMaxIngredients());
//    }
//
//    /**
//     * ✅ Valida precio final (solo si está modificando ingredientes)
//     */
//    private void validateBurgerPrice(BigDecimal finalPrice, BurgerSettings settings) {
//        if (finalPrice.compareTo(settings.getCustomBurgerMinPrice()) < 0) {
//            throw new InvalidBurgerException(
//                    String.format(
//                            "El precio de tu hamburguesa ($%,.0f) es menor al mínimo permitido ($%,.0f). " +
//                                    "Agrega más ingredientes o aumenta cantidades.",
//                            finalPrice,
//                            settings.getCustomBurgerMinPrice()
//                    )
//            );
//        }
//
//        if (finalPrice.compareTo(settings.getCustomBurgerMaxPrice()) > 0) {
//            throw new InvalidBurgerException(
//                    String.format(
//                            "El precio de tu hamburguesa ($%,.0f) excede el máximo permitido ($%,.0f). " +
//                                    "Reduce ingredientes o cantidades.",
//                            finalPrice,
//                            settings.getCustomBurgerMaxPrice()
//                    )
//            );
//        }
//
//        logger.debug("✅ Precio válido: ${} (Min: ${}, Max: ${})",
//                finalPrice, settings.getCustomBurgerMinPrice(), settings.getCustomBurgerMaxPrice());
//    }
//
//    // ==================== HELPER METHODS ====================
//
//    /**
//     * ✅ Detecta si se están modificando ingredientes (IDs o cantidades)
//     */
//    private boolean isModifyingIngredients(
//            List<UpdateCustomBurgerCommand.IngredientRequest> newIngredients,
//            List<Integer> currentIngredientIds
//    ) {
//        // Extraer IDs de los nuevos ingredientes
//        List<Integer> newIds = newIngredients.stream()
//                .map(UpdateCustomBurgerCommand.IngredientRequest::idProduct)
//                .sorted()
//                .toList();
//
//        // Ordenar IDs actuales para comparación
//        List<Integer> sortedCurrentIds = currentIngredientIds.stream()
//                .sorted()
//                .toList();
//
//        // Si los IDs son diferentes, hay cambio en ingredientes
//        boolean idsChanged = !newIds.equals(sortedCurrentIds);
//
//        logger.debug("🔍 IDs actuales: {}, IDs nuevos: {}, Cambió: {}",
//                sortedCurrentIds, newIds, idsChanged);
//
//        return idsChanged;
//    }
//
//    // ==================== VALIDACIONES BÁSICAS ====================
//
//    private void validateCommand(UpdateCustomBurgerCommand command) {
//        if (command == null) {
//            throw new InvalidBurgerException("El comando no puede ser nulo");
//        }
//
//        if (command.idBurger() == null) {
//            throw new InvalidBurgerException("El ID de la hamburguesa no puede ser nulo");
//        }
//
//        if (command.idUser() == null) {
//            throw new InvalidBurgerException("El ID del usuario no puede ser nulo");
//        }
//
//        if (command.name() == null || command.name().isBlank()) {
//            throw new InvalidBurgerException("El nombre no puede estar vacío");
//        }
//
//        if (command.ingredients() == null || command.ingredients().isEmpty()) {
//            throw new InvalidBurgerException(
//                    "La hamburguesa debe tener al menos un ingrediente"
//            );
//        }
//    }
//
//    private void validateUser(Integer userId) {
//        if (!userRepository.existsById(userId)) {
//            throw new UserNotFoundException(userId);
//        }
//    }
//
//    private void validateIsCustomBurger(Burger burger) {
//        if (!burger.isCustomBurger()) {
//            throw new InvalidBurgerException(
//                    "Solo hamburguesas personalizadas pueden actualizarse con este método. " +
//                            "Burger ID: " + burger.getIdBurger()
//            );
//        }
//
//        if (burger.isDeleted()) {
//            throw new InvalidBurgerException(
//                    "No se puede actualizar una hamburguesa eliminada. " +
//                            "Burger ID: " + burger.getIdBurger()
//            );
//        }
//    }
//
//    private void validateNoDuplicateIngredients(
//            List<UpdateCustomBurgerCommand.IngredientRequest> ingredients) {
//
//        long uniqueProducts = ingredients.stream()
//                .map(UpdateCustomBurgerCommand.IngredientRequest::idProduct)
//                .distinct()
//                .count();
//
//        if (uniqueProducts < ingredients.size()) {
//            throw new InvalidBurgerException(
//                    "La hamburguesa contiene ingredientes duplicados. " +
//                            "Si deseas más cantidad, aumenta el campo 'quantity'"
//            );
//        }
//    }
//
//    // ==================== CREAR INGREDIENTE ====================
//
//    private BurgerIngredient createBurgerIngredient(
//            UpdateCustomBurgerCommand.IngredientRequest request) {
//
//        logger.debug("🔍 Validando ingrediente: productId={}, quantity={}",
//                request.idProduct(), request.quantity());
//
//        // 1. Buscar producto
//        Product product = productRepository.findById(request.idProduct())
//                .orElseThrow(() -> new ProductNotFoundException(request.idProduct()));
//
//        // 2. Validar producto completo
//        validateProductForBurger(product, request.quantity());
//
//        // 3. Crear snapshot del producto
//        ProductSnapshot snapshot = ProductSnapshot.fromProduct(
//                product,
//                request.quantity()
//        );
//
//        logger.debug("✓ Ingrediente válido: {} x{} = ${}",
//                product.getName(),
//                request.quantity(),
//                snapshot.calculateSubtotal());
//
//        // 4. Crear ingrediente desde snapshot
//        return BurgerIngredient.fromSnapshot(snapshot);
//    }
//
//    private void validateProductForBurger(Product product, Integer quantity) {
//
//        // 1. Validar disponibilidad
//        if (!product.getAvailability()) {
//            throw new InvalidBurgerException(
//                    "El producto '" + product.getName() + "' no está disponible"
//            );
//        }
//
//        // 2. Validar que sea ingrediente de hamburguesa
//        if (product.getIsBurgerIngredient() == null || !product.getIsBurgerIngredient()) {
//            throw new InvalidBurgerException(
//                    "El producto '" + product.getName() + "' no es un ingrediente de hamburguesa"
//            );
//        }
//
//        // 3. Validar tipo de producto
//        if (!ALLOWED_INGREDIENT_TYPES.contains(product.getProductType())) {
//            throw new InvalidBurgerException(
//                    "El producto '" + product.getName() + "' debe ser de tipo INGREDIENT. " +
//                            "Tipo actual: " + product.getProductType()
//            );
//        }
//
//        // 4. Validar stock disponible
//        if (product.getQuantity() <= 0) {
//            throw new InsufficientStockException(
//                    "El producto '" + product.getName() + "' no tiene stock disponible"
//            );
//        }
//
//        // 5. Validar cantidad suficiente
//        if (product.getQuantity() < quantity) {
//            throw new InsufficientStockException(
//                    "Stock insuficiente para '" + product.getName() + "'. " +
//                            "Disponible: " + product.getQuantity() + ", Solicitado: " + quantity
//            );
//        }
//
//        logger.debug("✅ Producto validado: {} | Categoría: {} | Stock: {} | Solicitado: {}",
//                product.getName(),
//                product.getCategoryName(),
//                product.getQuantity(),
//                quantity);
//    }
//}
