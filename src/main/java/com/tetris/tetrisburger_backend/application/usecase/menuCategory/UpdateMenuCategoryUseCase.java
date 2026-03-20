package com.tetris.tetrisburger_backend.application.usecase.menuCategory;

import com.tetris.tetrisburger_backend.domain.exception.EntityNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.MenuCategory;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.UpdateMenuCategory;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.command.UpdateMenuCategoryCommand;
import com.tetris.tetrisburger_backend.domain.port.out.MenuCategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UpdateMenuCategoryUseCase implements UpdateMenuCategory {

    private final MenuCategoryRepository repository;

    public UpdateMenuCategoryUseCase(MenuCategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public MenuCategory handle(Integer id, UpdateMenuCategoryCommand command) {
        MenuCategory category = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "MenuCategory no encontrada con id: " + id));

        if (repository.existsByNameAndDeletedAtIsNull(command.menuCategoryName())
                && !category.getMenuCategoryName().equalsIgnoreCase(command.menuCategoryName()))
            throw new IllegalArgumentException(
                    "Ya existe una categoría activa con el nombre: " + command.menuCategoryName());

        category.update(command.menuCategoryName(), command.description());

        return repository.save(category);
    }
}
