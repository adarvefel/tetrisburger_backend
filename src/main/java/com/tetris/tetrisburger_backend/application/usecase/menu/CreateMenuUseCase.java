package com.tetris.tetrisburger_backend.application.usecase.menu;

import com.tetris.tetrisburger_backend.application.event.MenuImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.enums.ItemType;
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
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        // ── 1. Recolectar IDs únicos — 2 queries en total, no N ──────────────
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

        // ── 2. Validar que todos existan ──────────────────────────────────────
        burgerIds.forEach(id -> {
            if (!burgerMap.containsKey(id))
                throw new IllegalArgumentException("Burger no encontrada: " + id);
        });

        productIds.forEach(id -> {
            if (!productMap.containsKey(id))
                throw new IllegalArgumentException("Producto no encontrado: " + id);
        });

        // ── 3. Construir ítems desde el map — O(1) por lookup ─────────────────
        List<MenuItem> items = command.items().stream()
                .map(i -> MenuItem.create(
                        ItemType.valueOf(i.itemType().toUpperCase()),
                        i.idBurger()  != null ? burgerMap.get(i.idBurger())   : null,
                        i.idProduct() != null ? productMap.get(i.idProduct()) : null
                ))
                .toList();

        Menu menu = Menu.create(
                command.name(),
                command.description(),
                command.isAvailable(),
                null,
                null,
                menuCategory,
                items,
                command.createdBy()
        );

        Menu saved = menuRepository.save(menu);

        if (command.imageData() != null) {
            eventPublisher.publishEvent(new MenuImageUploadRequestedEvent(
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