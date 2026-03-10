package com.tetris.tetrisburger_backend.application.usecase.burger.client;

import com.tetris.tetrisburger_backend.domain.exception.BurgerCreationException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidBurgerException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidSettingsException;
import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.BurgerSettings;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.model.ProductType;
import com.tetris.tetrisburger_backend.domain.port.in.burger.client.CreateCustomBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.client.command.CreateCustomBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import com.tetris.tetrisburger_backend.domain.port.out.SettingsRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CreateCustomBurgerUseCase implements CreateCustomBurger {

    private final BurgerRepository burgerRepository;
    private final ProductRepository productRepository;
    private final SettingsRepository settingsRepository;

    public CreateCustomBurgerUseCase(
            BurgerRepository burgerRepository,
            ProductRepository productRepository,
            SettingsRepository settingsRepository
    ) {
        this.burgerRepository = burgerRepository;
        this.productRepository = productRepository;
        this.settingsRepository = settingsRepository;
    }

    @Override
    public Burger handle(CreateCustomBurgerCommand command) {

        // 1. Obtener settings
        BurgerSettings settings = settingsRepository.getBurgerSettings();

        // 2. Validar que custom burgers estén habilitadas
        if (!settings.isCustomBurgersEnabled()) {
            throw new InvalidSettingsException(
                    "Las hamburguesas personalizadas están deshabilitadas temporalmente"
            );
        }

        // 3. Validar cantidad de ingredientes
        int total = command.ingredients().size();
        if (total < settings.getMinIngredients()) {
            throw new InvalidSettingsException(
                    "Mínimo " + settings.getMinIngredients() + " ingredientes requeridos. " +
                            "Tienes " + total
            );
        }
        if (total > settings.getMaxIngredients()) {
            throw new InvalidSettingsException(
                    "Máximo " + settings.getMaxIngredients() + " ingredientes permitidos. " +
                            "Tienes " + total
            );
        }

        // 4. Validar duplicados
        long unique = command.ingredients().stream()
                .map(CreateCustomBurgerCommand.IngredientRequest::idProduct)
                .distinct().count();
        if (unique < total) {
            throw new InvalidBurgerException(
                    "No puedes repetir ingredientes. " +
                            "Aumenta la cantidad si quieres más de uno"
            );
        }

        // 5. Construir burger
        Burger.CustomBuilder builder = new Burger.CustomBuilder(
                command.name(),
                command.idUser()
        );


        for (var req : command.ingredients()) {
            Product product = productRepository.findById(req.idProduct())
                    .orElseThrow(() -> new ProductNotFoundException(req.idProduct()));

            validateProduct(product);

            ProductSnapshot snapshot = ProductSnapshot.fromProduct(
                    product,
                    req.quantity(),
                    false);
            builder.addIngredient(snapshot);
        }

        Burger burger = builder.build();

        // 6. Validar precio contra settings
        settings.validatePriceForNew(burger.getBasePrice());

        // 7. Guardar
        Burger saved = burgerRepository.save(burger);

        if (saved == null || saved.getIdBurger() == null) {
            throw new BurgerCreationException("Error al guardar la hamburguesa personalizada");
        }

        return saved;
    }

    private void validateProduct(Product product) {
        if (product.getProductType() != ProductType.INGREDIENT) {
            throw new InvalidBurgerException(
                    "'" + product.getName() + "' no es un ingrediente válido. " +
                            "Tipo: " + product.getProductType()
            );
        }
        if (!product.getAvailability()) {
            throw new InvalidBurgerException(
                    "'" + product.getName() + "' no está disponible actualmente"
            );
        }
        if (product.isDeleted()) {
            throw new InvalidBurgerException(
                    "'" + product.getName() + "' no existe"
            );
        }
    }
}