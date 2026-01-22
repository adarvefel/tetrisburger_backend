package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;
import com.tetris.tetrisburger_backend.domain.port.in.burger.CreateMenuBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.CreateBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CreateBurgerUseCase implements CreateMenuBurger {

    private final BurgerRepository  burgerRepository;
    private final ProductRepository productRepository;

    public CreateBurgerUseCase(BurgerRepository burgerRepository, ProductRepository productRepository) {
        this.burgerRepository = burgerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Burger handle(CreateBurgerCommand command) {
        List<ProductSnapshot> snapshots = command.ingredients().stream()
                .map(ing -> {
                    var product = productRepository.findById(ing.idProduct())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Producto no encontrado: " + ing.idProduct()
                            ));
                    return ProductSnapshot.fromProduct(
                            product,
                            ing.quantity(),
                            ing.isOptional()
                    );
                })
                .toList();

        // Aquí puedes tener un builder distinto o un factory estático
        Burger burger = Burger.menuBurger(
                command.name(),
                command.description(),
                command.imageUrl(),
                snapshots,
                command.isFavorite()
        );
        if (command.finalPrice() != null &&
                command.finalPrice().compareTo(BigDecimal.ZERO) > 0) {
            burger.updatePrice(command.finalPrice());
        }

        return burgerRepository.save(burger);
    }


}
