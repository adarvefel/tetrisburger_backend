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

        // 3. Construir ítems con objetos completos
        List<MenuItem> items = command.items().stream()
                .map(i -> {
                    Burger burger = null;
                    Product product = null;

                    if (i.idBurger() != null)
                        burger = burgerRepository.findById(i.idBurger())
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Burger no encontrada: " + i.idBurger()));

                    if (i.idProduct() != null)
                        product = productRepository.findById(i.idProduct())
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Producto no encontrado: " + i.idProduct()));

                    return MenuItem.create(
                            ItemType.valueOf(i.itemType().toUpperCase()),
                            burger,
                            product,
                            i.quantity()
                    );
                })
                .toList();

        // 4. Actualizar el menú
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
