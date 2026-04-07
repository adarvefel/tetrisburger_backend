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
    public MenuCategory handle(Integer id,Integer deletedBy) {
        MenuCategory category = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "La categoria de menu no fue encontrada con id: " + id
                ));

        category.softDelete(deletedBy);

        repository.delete(category);
        return category;
    }

}