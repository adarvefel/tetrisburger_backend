package com.tetris.tetrisburger_backend.domain.port.in.menucategory;

import com.tetris.tetrisburger_backend.domain.model.MenuCategory;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.command.CreateMenuCategoryCommand;

public interface CreateMenuCategory {
    MenuCategory create(CreateMenuCategoryCommand command);

}
