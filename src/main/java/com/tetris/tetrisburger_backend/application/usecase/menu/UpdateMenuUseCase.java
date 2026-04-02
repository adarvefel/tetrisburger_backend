package com.tetris.tetrisburger_backend.application.usecase.menu;

import com.tetris.tetrisburger_backend.domain.enums.ItemType;
import com.tetris.tetrisburger_backend.domain.exception.EntityNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.*;
import com.tetris.tetrisburger_backend.domain.port.in.menu.UpdateMenu;
import com.tetris.tetrisburger_backend.domain.port.in.menu.command.UpdateMenuCommand;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.MenuCategoryRepository;
import com.tetris.tetrisburger_backend.domain.port.out.MenuRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class UpdateMenuUseCase implements UpdateMenu {

    private final MenuRepository menuRepository;
    private final BurgerRepository burgerRepository;
    private final ProductRepository productRepository;
    private final MenuCategoryRepository menuCategoryRepository;

    public UpdateMenuUseCase(MenuRepository menuRepository,
                             BurgerRepository burgerRepository,
                             ProductRepository productRepository,
                             MenuCategoryRepository menuCategoryRepository) {
        this.menuRepository = menuRepository;
        this.burgerRepository = burgerRepository;
        this.productRepository = productRepository;
        this.menuCategoryRepository = menuCategoryRepository;
    }

    @Override
    public Menu handle(UpdateMenuCommand command) {

        // 1. Buscar menú existente
        Menu menu = menuRepository.findById(command.idMenu())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Menú no encontrado con id: " + command.idMenu()));

        // 2. Buscar categoría si viene
        MenuCategory menuCategory = null;
        if (command.idMenuCategory() != null)
            menuCategory = menuCategoryRepository.findById(command.idMenuCategory())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Categoría no encontrada: " + command.idMenuCategory()));

        // 3. Recolectar IDs únicos — 2 queries en total, no N
        List<Integer> burgerIds = command.items().stream()
                .map(i -> i.idBurger())
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<Integer> productIds = command.items().stream()
                .map(i -> i.idProduct())
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Integer, Burger> burgerMap = burgerRepository.findAllByIds(burgerIds)
                .stream()
                .collect(Collectors.toMap(Burger::getIdBurger, Function.identity()));

        Map<Integer, Product> productMap = productRepository.findAllByIds(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        // 4. Validar que todos existan
        burgerIds.forEach(id -> {
            if (!burgerMap.containsKey(id))
                throw new IllegalArgumentException("Burger no encontrada: " + id);
        });

        productIds.forEach(id -> {
            if (!productMap.containsKey(id))
                throw new IllegalArgumentException("Producto no encontrado: " + id);
        });

        List<MenuItem> items = command.items().stream()
                .map(i -> MenuItem.create(
                        ItemType.valueOf(i.itemType().toUpperCase()),
                        i.idBurger()  != null ? burgerMap.get(i.idBurger())   : null,
                        i.idProduct() != null ? productMap.get(i.idProduct()) : null,
                        i.quantity()
                ))
                .toList();

        // 6. Actualizar el menú
        menu.update(
                command.name(),
                command.description(),
                command.isAvailable(),
                menuCategory,
                items,
                command.updatedBy()
        );

        return menuRepository.update(menu);
    }
}