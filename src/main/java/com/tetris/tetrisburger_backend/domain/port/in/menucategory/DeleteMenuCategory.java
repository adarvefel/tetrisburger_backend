package com.tetris.tetrisburger_backend.domain.port.in.menucategory;

import com.tetris.tetrisburger_backend.domain.model.MenuCategory;

public interface DeleteMenuCategory {
    MenuCategory handle(Integer id);
}
