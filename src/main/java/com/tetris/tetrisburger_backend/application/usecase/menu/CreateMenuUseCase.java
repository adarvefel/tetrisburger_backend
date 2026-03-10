package com.tetris.tetrisburger_backend.application.usecase.menu;

import com.tetris.tetrisburger_backend.application.event.MenuBurgerImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.model.*;
import com.tetris.tetrisburger_backend.domain.port.in.menu.CreateMenu;
import com.tetris.tetrisburger_backend.domain.port.in.menu.command.CreateMenuCommand;
import com.tetris.tetrisburger_backend.domain.port.out.MenuCategoryRepository;
import com.tetris.tetrisburger_backend.domain.port.out.MenuRepository;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CreateMenuUseCase implements CreateMenu {

    private final MenuRepository menuRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final BurgerRepository burgerRepository;
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CreateMenuUseCase(MenuRepository menuRepository,
                             MenuCategoryRepository menuCategoryRepository,
                             BurgerRepository burgerRepository,
                             ProductRepository productRepository,
                             ApplicationEventPublisher eventPublisher) {
        this.menuRepository = menuRepository;
        this.menuCategoryRepository = menuCategoryRepository;
        this.burgerRepository = burgerRepository;
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Menu handle(CreateMenuCommand command) {

        MenuCategory menuCategory = command.idMenuCategory() != null
                ? menuCategoryRepository.findById(command.idMenuCategory())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Categoría de menú no encontrada: " + command.idMenuCategory()))
                : null;

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


        Menu menu = Menu.create(
                command.name(),
                command.description(),
                command.isAvailable(),
                null,           // imageUrl
                null,           // imageKey
                menuCategory,
                items,
                command.createdBy()
        );

        Menu saved = menuRepository.save(menu);

        if (command.imageData() != null) {
            eventPublisher.publishEvent(new MenuBurgerImageUploadRequestedEvent(
                    saved.getIdMenu(),
                    command.imageData().bytes(),
                    command.imageData().contentType(),
                    command.imageData().originalFilename(),
                    command.createdBy()
            ));
        }

        return saved;
    }
}