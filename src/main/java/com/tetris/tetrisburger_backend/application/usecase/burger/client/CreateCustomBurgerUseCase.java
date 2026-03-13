package com.tetris.tetrisburger_backend.application.usecase.burger.client;

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

import java.util.stream.Collectors;

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

        // 1. Obtener configuración global del sistema
        BurgerSettings settings = settingsRepository.getBurgerSettings();

        // 2. Validar que la funcionalidad esté activa
        if (!settings.isCustomBurgersEnabled()) {
            throw new InvalidSettingsException("Las hamburguesas personalizadas están deshabilitadas temporalmente");
        }

        // 3. LIMPIEZA: Eliminamos cualquier borrador (draft) previo del usuario.
        // Esto evita el error de "NonUniqueResultException" al asegurar que
        // siempre partimos de cero.
        burgerRepository.deleteActiveDraftsByUser(command.idUser());

        // 4. Validar límites de cantidad de ingredientes
        int totalIngredients = command.ingredients().size();
        if (totalIngredients < settings.getMinIngredients()) {
            throw new InvalidSettingsException("Mínimo " + settings.getMinIngredients() + " ingredientes requeridos.");
        }
        if (totalIngredients > settings.getMaxIngredients()) {
            throw new InvalidSettingsException("Máximo " + settings.getMaxIngredients() + " ingredientes permitidos.");
        }

        // 5. Validar que no existan ingredientes duplicados en el comando
        boolean hasDuplicates = command.ingredients().stream()
                .map(CreateCustomBurgerCommand.IngredientRequest::idProduct)
                .collect(Collectors.toSet())
                .size() < totalIngredients;

        if (hasDuplicates) {
            throw new InvalidBurgerException("No puedes repetir ingredientes. Incrementa la cantidad del ingrediente existente.");
        }

        // 6. Construcción de la Hamburguesa usando el Domain Builder
        Burger.CustomBuilder builder = new Burger.CustomBuilder(command.name(), command.idUser());

        for (var req : command.ingredients()) {
            Product product = productRepository.findById(req.idProduct())
                    .orElseThrow(() -> new ProductNotFoundException(req.idProduct()));

            validateProduct(product);

            // Creamos el snapshot para persistir el estado actual del producto (precio/nombre)
            ProductSnapshot snapshot = ProductSnapshot.fromProduct(product, req.quantity(), false);
            builder.addIngredient(snapshot);
        }

        Burger burger = builder.build();

        // 7. Validar que el precio resultante cumpla con las políticas de negocio
        settings.validatePriceForNew(burger.getBasePrice());

        // 8. Persistir el nuevo borrador
        return burgerRepository.save(burger);
    }

    private void validateProduct(Product product) {
        if (product.isDeleted()) {
            throw new InvalidBurgerException("El producto '" + product.getName() + "' ya no existe.");
        }
        if (product.getProductType() != ProductType.INGREDIENT) {
            throw new InvalidBurgerException("'" + product.getName() + "' no es un ingrediente válido.");
        }
        if (!product.getAvailability()) {
            throw new InvalidBurgerException("'" + product.getName() + "' no está disponible actualmente.");
        }
    }
}