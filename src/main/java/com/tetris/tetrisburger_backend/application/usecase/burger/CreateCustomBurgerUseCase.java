package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;
import com.tetris.tetrisburger_backend.domain.port.in.burger.CreateCustomBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.CreateCustomBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.IngredientRequest;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CreateCustomBurgerUseCase implements CreateCustomBurger {

    private static final Logger logger = LoggerFactory.getLogger(CreateCustomBurgerUseCase.class);

    private final BurgerRepository burgerRepository;
    private final ProductRepository productRepository;

    public CreateCustomBurgerUseCase(BurgerRepository burgerRepository,
                                     ProductRepository productRepository) {
        this.burgerRepository = burgerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Burger handle(CreateCustomBurgerCommand command) {
        // 1. Construir snapshots a partir de los Product
        List<ProductSnapshot> snapshots = command.ingredients().stream()
                .map(this::toSnapshot)
                .toList();

        // 2. Usar el CustomBuilder del agregado Burger
        Burger.CustomBuilder builder = new Burger.CustomBuilder(command.name(), command.idUser())
                .withDescription(command.description())
                .withImage(command.imageUrl());

        for (ProductSnapshot snapshot : snapshots) {
            builder.addIngredient(snapshot);
        }

        Burger burger = builder.build();

        logger.info("CreateCustomBurgerUseCase -> antes de save: idBurger={}, ingredients.size={}, finalPrice={}",
                burger.getIdBurger(), burger.getIngredients().size(), burger.getFinalPrice());

        // 3. Persistir el agregado
        return burgerRepository.save(burger);
    }

    private ProductSnapshot toSnapshot(IngredientRequest request) {
        Product product = productRepository.findById(request.idProduct())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Producto no encontrado: " + request.idProduct()
                ));

        return ProductSnapshot.fromProduct(
                product,
                request.quantity(),
                request.isOptional()
        );
    }
}
