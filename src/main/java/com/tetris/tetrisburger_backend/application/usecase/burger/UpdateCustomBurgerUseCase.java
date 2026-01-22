package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.BurgerIngredient;
import com.tetris.tetrisburger_backend.domain.port.in.burger.UpdateCustomBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateCustomBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class UpdateCustomBurgerUseCase implements UpdateCustomBurger {

    private static final Logger logger = LoggerFactory.getLogger(UpdateCustomBurgerUseCase.class);

    private final BurgerRepository burgerRepository;
    private final ProductRepository productRepository;

    public UpdateCustomBurgerUseCase(
            BurgerRepository burgerRepository,
            ProductRepository productRepository
    ) {
        this.burgerRepository = burgerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Burger handle(UpdateCustomBurgerCommand command) {
        logger.info("Actualizando burger custom id={} para user={}",
                command.idBurger(), command.idUser());

        // 1. Cargar burger y validar dueño + que sea custom
        Burger burger = burgerRepository.findCustomByIdAndUser(
                        command.idBurger(), command.idUser())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Burger no encontrada o no pertenece al usuario"));

        // 2. Actualizar ingredientes (reemplazar lista)
        List<BurgerIngredient> newIngredients = command.ingredients().stream()
                .map(req -> {
                    var product = productRepository.findById(req.idProduct())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Producto no encontrado con id " + req.idProduct()));

                    BigDecimal priceAtTime = product.getPrice(); // o snapshot.getPrice()

                    return BurgerIngredient.create(
                            req.idProduct(),
                            priceAtTime,
                            req.quantity(),
                            req.isOptional()
                    );
                })
                .toList();

        // 3. Delegar la actualización al dominio (método que agregaste en Burger)
        burger.updateCustomBurger(
                command.idUser(),
                command.name(),
                command.description(),
                command.imageUrl(),
                newIngredients
        );

        // 4. Guardar
        Burger updated = burgerRepository.save(burger);

        logger.info("Burger custom actualizada id={} para user={}",
                updated.getIdBurger(), command.idUser());
        return updated;
    }
}
