package com.tetris.tetrisburger_backend.application.usecase.menu;

import com.tetris.tetrisburger_backend.domain.exception.EntityNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Menu;
import com.tetris.tetrisburger_backend.domain.model.MenuItem;
import com.tetris.tetrisburger_backend.domain.port.in.menu.UpdateMenu;
import com.tetris.tetrisburger_backend.domain.port.in.menu.command.UpdateMenuCommand;
import com.tetris.tetrisburger_backend.domain.port.out.MenuRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UpdateMenuUseCase implements UpdateMenu {

    private final MenuRepository repository;

    public UpdateMenuUseCase(MenuRepository repository) {
        this.repository = repository;
    }

    @Override
    public Menu handle(UpdateMenuCommand command) {
        Menu menu = repository.findById(command.idMenu())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Menú no encontrado con id: " + command.idMenu()
                ));

        List<MenuItem> items = command.items().stream()
                .map(i -> MenuItem.create(i.itemType(), i.idBurger(), i.idProduct(), i.quantity()))
                .toList();

        menu.update(
                command.name(),
                command.description(),
                command.regularPrice(),
                command.comboPrice(),
                command.isAvailable(),
                command.imageUrl(),
                command.imageKey(),
                command.idMenuCategory(),
                items,
                command.updatedBy()
        );

        return repository.save(menu);
    }
}