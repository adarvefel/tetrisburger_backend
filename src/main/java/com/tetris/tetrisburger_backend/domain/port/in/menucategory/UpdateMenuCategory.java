package com.tetris.tetrisburger_backend.domain.port.in.menucategory;

import com.tetris.tetrisburger_backend.domain.model.MenuCategory;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.command.UpdateMenuCategoryCommand;

public interface UpdateMenuCategory {
    MenuCategory handle(Integer id,UpdateMenuCategoryCommand command);
}
