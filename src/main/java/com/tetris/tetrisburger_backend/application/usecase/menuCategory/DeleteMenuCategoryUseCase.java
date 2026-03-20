package com.tetris.tetrisburger_backend.application.usecase.menuCategory;

import com.tetris.tetrisburger_backend.domain.model.MenuCategory;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.DeleteMenuCategory;
import com.tetris.tetrisburger_backend.domain.port.out.MenuCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DeleteMenuCategoryUseCase implements DeleteMenuCategory {

    private final MenuCategoryRepository repository;

    public DeleteMenuCategoryUseCase(MenuCategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public MenuCategory handle(Integer id) {
        MenuCategory category = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "MenuCategory no encontrada con id: " + id
                ));

        category.softDelete();

        repository.delete(category);
        return category;
    }

}