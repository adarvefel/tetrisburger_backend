package com.tetris.tetrisburger_backend.application.usecase.burger.admin;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.SearchMenuBurgers;
import com.tetris.tetrisburger_backend.domain.port.in.burger.query.SearchMenuBurgersQuery;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SearchMenuBurgersUseCase implements SearchMenuBurgers {

    private final BurgerRepository burgerRepository;

    public SearchMenuBurgersUseCase(BurgerRepository burgerRepository) {
        this.burgerRepository = burgerRepository;
    }

    @Override
    public PageResponse<Burger> search(SearchMenuBurgersQuery query, PaginationRequest pagination) {
        return burgerRepository.searchMenuByName(query.name(), pagination);
    }
}
