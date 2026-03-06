package com.tetris.tetrisburger_backend.application.usecase.menu;

import com.tetris.tetrisburger_backend.domain.model.Menu;
import com.tetris.tetrisburger_backend.domain.port.in.menu.DeleteMenu;
import com.tetris.tetrisburger_backend.domain.port.out.MenuRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DeleteMenuUseCase implements DeleteMenu {

    private final MenuRepository repository;

    public DeleteMenuUseCase(MenuRepository repository) {
        this.repository = repository;
    }

    @Override
    public Menu handle(Integer id, Integer deletedBy) {
        Menu menu = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Menú no encontrado con id: " + id
                ));

        menu.softDelete(deletedBy);
        repository.delete(menu);
        return menu;
    }
}