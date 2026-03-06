package com.tetris.tetrisburger_backend.application.usecase.menu;

import com.tetris.tetrisburger_backend.application.event.MenuBurgerImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.model.Menu;
import com.tetris.tetrisburger_backend.domain.model.MenuItem;
import com.tetris.tetrisburger_backend.domain.port.in.menu.CreateMenu;
import com.tetris.tetrisburger_backend.domain.port.in.menu.command.CreateMenuCommand;
import com.tetris.tetrisburger_backend.domain.port.out.MenuRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Transactional
public class CreateMenuUseCase implements CreateMenu {

    private final MenuRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public CreateMenuUseCase(MenuRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Menu handle(CreateMenuCommand command) {
        List<MenuItem> items = command.items().stream()
                .map(i -> MenuItem.create(i.itemType(), i.idBurger(), i.idProduct(), i.quantity()))
                .toList();

        Menu menu = Menu.create(
                command.name(),
                command.description(),
                command.regularPrice(),
                command.comboPrice(),
                command.isAvailable(),
                null,  // imageUrl
                null,  // imageKey
                command.idMenuCategory(),

                items,
                command.createdBy()
        );

        Menu saved = repository.save(menu);

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