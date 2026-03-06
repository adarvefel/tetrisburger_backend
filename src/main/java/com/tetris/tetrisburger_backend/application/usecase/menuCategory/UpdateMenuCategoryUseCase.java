package com.tetris.tetrisburger_backend.application.usecase.menuCategory;

import com.tetris.tetrisburger_backend.domain.model.MenuCategory;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.UpdateMenuCategory;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.command.UpdateMenuCategoryCommand;
import com.tetris.tetrisburger_backend.domain.port.out.MenuCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UpdateMenuCategoryUseCase implements UpdateMenuCategory {

    private static final Logger log = LoggerFactory.getLogger(UpdateMenuCategoryUseCase.class);
    private final MenuCategoryRepository repository;

    public UpdateMenuCategoryUseCase(MenuCategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public MenuCategory handle(Integer id,UpdateMenuCategoryCommand command) {
        MenuCategory category = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "MenuCategory no encontrada con id: " + id
                ));

        category.update(command.menuCategoryName(), command.description());

        return repository.save(category);
    }
}