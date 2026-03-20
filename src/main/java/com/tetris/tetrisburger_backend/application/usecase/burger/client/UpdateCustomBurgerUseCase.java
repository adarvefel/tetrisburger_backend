package com.tetris.tetrisburger_backend.application.usecase.burger.client;

import com.tetris.tetrisburger_backend.domain.exception.*;
import com.tetris.tetrisburger_backend.domain.model.*;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.UpdateCustomBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;
import com.tetris.tetrisburger_backend.domain.port.in.burger.client.command.UpdateCustomBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import com.tetris.tetrisburger_backend.domain.port.out.SettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.List;

@Service
@Transactional
public class UpdateCustomBurgerUseCase implements UpdateCustomBurger {

    private static final Logger logger = LoggerFactory.getLogger(UpdateCustomBurgerUseCase.class);

    private final BurgerRepository burgerRepository;
    private final ProductRepository productRepository;
    private final SettingsRepository settingsRepository;

    public UpdateCustomBurgerUseCase(BurgerRepository burgerRepository,
                                     ProductRepository productRepository,
                                     SettingsRepository settingsRepository) {
        this.burgerRepository = burgerRepository;
        this.productRepository = productRepository;
        this.settingsRepository = settingsRepository;
    }

    @Override
    public Burger handle(UpdateCustomBurgerCommand command) {

        logger.info("Actualizando burger personalizada: idBurger={} | idUser={}",
                command.idBurger(), command.idUser());

        // 1. Obtener settings
        BurgerSettings settings = settingsRepository.getBurgerSettings();

        if (!settings.isCustomBurgersEnabled())
            throw new InvalidSettingsException(
                    "Las hamburguesas personalizadas están deshabilitadas temporalmente");

        // 2. Buscar la burger del usuario (sin filtro de isSaved)
        Burger burger = burgerRepository.findCustomByIdAndUser(command.idBurger(), command.idUser())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Hamburguesa personalizada no encontrada con id: " + command.idBurger()));

        // 3. Validar cantidad de ingredientes
        int total = command.ingredients().size();
        if (total < settings.getMinIngredients())
            throw new InvalidSettingsException(
                    "Mínimo " + settings.getMinIngredients() + " ingredientes requeridos. Tienes " + total);
        if (total > settings.getMaxIngredients())
            throw new InvalidSettingsException(
                    "Máximo " + settings.getMaxIngredients() + " ingredientes permitidos. Tienes " + total);

        // 4. Validar duplicados
        boolean hasDuplicates = command.ingredients().stream()
                .map(UpdateCustomBurgerCommand.IngredientRequest::idProduct)
                .collect(Collectors.toSet())
                .size() < total;

        if (hasDuplicates)
            throw new InvalidBurgerException(
                    "No puedes repetir ingredientes. Aumenta la cantidad si quieres más de uno");

        // 5. Construir nuevos ingredientes
        List<BurgerIngredient> newIngredients = new ArrayList<>();
        for (var req : command.ingredients()) {
            Product product = productRepository.findById(req.idProduct())
                    .orElseThrow(() -> new ProductNotFoundException(req.idProduct()));
            validateProduct(product);
            newIngredients.add(BurgerIngredient.fromSnapshot(
                    ProductSnapshot.fromProduct(product, req.quantity(), false)));
        }

        // 6. Actualizar en el dominio
        burger.updateCustomBurger(
                command.idUser(),
                command.name(),
                newIngredients
        );

        // 7. Validar precio contra settings
        settings.validatePriceForNew(burger.getBasePrice());

        // 8. Guardar
        Burger saved = burgerRepository.save(burger);

        logger.info("Burger personalizada actualizada: idBurger={} | idUser={}",
                saved.getIdBurger(), command.idUser());

        return saved;
    }

    // ==================== VALIDACIONES ====================

    private void validateProduct(Product product) {
        if (product.isDeleted())
            throw new InvalidBurgerException("'" + product.getName() + "' no existe");
        if (product.getProductType() != ProductType.INGREDIENT)
            throw new InvalidBurgerException(
                    "'" + product.getName() + "' no es un ingrediente válido. Tipo: " + product.getProductType());
        if (!product.getAvailability())
            throw new InvalidBurgerException(
                    "'" + product.getName() + "' no está disponible actualmente");
    }
}
