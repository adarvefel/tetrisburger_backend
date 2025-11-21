package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.BurgerIngredient;
import com.tetris.tetrisburger_backend.domain.port.in.burger.UpdateMenuBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateMenuBurgerCommand;
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
public class UpdateMenuBurgerUseCase implements UpdateMenuBurger {

    private static final Logger logger = LoggerFactory.getLogger(UpdateMenuBurgerUseCase.class);

    private final BurgerRepository burgerRepository;
    private final ProductRepository productRepository;

    public UpdateMenuBurgerUseCase(BurgerRepository burgerRepository,
                                   ProductRepository productRepository) {
        this.burgerRepository = burgerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Burger handle(UpdateMenuBurgerCommand command) {
        logger.info("Actualizando burger de menú id={}", command.idBurger());

        Burger burger = burgerRepository.findActiveMenuById(command.idBurger())
                .orElseThrow(() -> new IllegalArgumentException("Burger de menú no encontrada"));

        List<BurgerIngredient> newIngredients = command.ingredients().stream()
                .map(req -> {
                    var product = productRepository.findById(req.idProduct())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Producto no encontrado con id " + req.idProduct()));

                    BigDecimal priceAtTime = product.getPrice();

                    return BurgerIngredient.create(
                            req.idProduct(),
                            priceAtTime,
                            req.quantity(),
                            req.isOptional()
                    );
                })
                .toList();

        burger.updateMenuBurger(
                command.name(),
                command.description(),
                command.imageUrl(),
                newIngredients,
                command.availability(),
                command.onMenu(),
                command.favorite()
        );

        Burger updated = burgerRepository.save(burger);
        logger.info("Burger de menú actualizada id={}", updated.getIdBurger());
        return updated;
    }

}
