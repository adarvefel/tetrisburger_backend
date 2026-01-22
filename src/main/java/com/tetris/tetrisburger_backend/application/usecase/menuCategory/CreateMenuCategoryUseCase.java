package com.tetris.tetrisburger_backend.application.usecase.menuCategory;


import com.tetris.tetrisburger_backend.domain.model.MenuCategory;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.CreateMenuCategory;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.command.CreateMenuCategoryCommand;
import com.tetris.tetrisburger_backend.domain.port.out.MenuCategoryRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CreateMenuCategoryUseCase implements CreateMenuCategory {
    private static final Logger logger = LoggerFactory.getLogger(CreateMenuCategory.class);

    private final MenuCategoryRepository repository;

    public CreateMenuCategoryUseCase(MenuCategoryRepository repository) {
        this.repository = repository;
    }


    @Override
    public MenuCategory create(CreateMenuCategoryCommand command) {
        logger.info("Creando categoria de menu:{}",command.menuCategoryName());
        MenuCategory category = MenuCategory.create(
                command.menuCategoryName(),
                command.description()
        );
        MenuCategory saved = repository.save(category);

        logger.info("Categoria de menu creada: ID={}, Nombre={}, Descripcion={}",saved.getIdMenuCategory(),
                saved.getMenuCategoryName(),saved.getDescription());

        return  saved;
    }
}
