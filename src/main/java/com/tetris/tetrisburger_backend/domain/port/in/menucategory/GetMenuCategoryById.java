package com.tetris.tetrisburger_backend.domain.port.in.menucategory;

import com.tetris.tetrisburger_backend.domain.model.MenuCategory;


public interface GetMenuCategoryById {
    MenuCategory handle(Integer id);
}
