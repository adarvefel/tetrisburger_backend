package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.MenuCategory;

import java.util.Optional;

public interface MenuCategoryRepository {
    MenuCategory save(MenuCategory menuCategory);
    Optional<MenuCategory> findById(Integer id);

    boolean existsByNameAndDeletedAtIsNull(String name);

    PageResponse<MenuCategory> findAll(PaginationRequest pagination);
    void delete(MenuCategory menuCategory);
}