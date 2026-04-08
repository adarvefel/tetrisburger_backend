package com.tetris.tetrisburger_backend.application.usecase.menu;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Menu;
import com.tetris.tetrisburger_backend.domain.port.in.menu.ListMenu;
import com.tetris.tetrisburger_backend.domain.port.out.MenuRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ListMenuUseCase implements ListMenu {

    private final MenuRepository repository;

    public ListMenuUseCase(MenuRepository repository) {
        this.repository = repository;
    }

    @Override
    public PageResponse<Menu> handle(PaginationRequest request) {
        return repository.findAll(request);
    }
}