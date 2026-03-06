package com.tetris.tetrisburger_backend.application.usecase.menuCategory;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.MenuCategory;
import com.tetris.tetrisburger_backend.domain.port.in.menucategory.ListMenuCategory;
import com.tetris.tetrisburger_backend.domain.port.out.MenuCategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class ListMenuCategoryUseCase implements ListMenuCategory {

    private final MenuCategoryRepository repository;

    public ListMenuCategoryUseCase(MenuCategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public PageResponse<MenuCategory> handle(PaginationRequest request) {
        return repository.findAll(request);
    }
}