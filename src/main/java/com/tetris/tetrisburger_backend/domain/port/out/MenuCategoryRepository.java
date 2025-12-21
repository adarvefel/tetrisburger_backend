package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.model.MenuCategory;

public interface MenuCategoryRepository {
    MenuCategory save(MenuCategory menuCategory );
}
