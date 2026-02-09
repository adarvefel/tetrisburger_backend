// src/main/java/com/tetris/tetrisburger_backend/application/usecase/burger/SearchMenuBurgersUseCase.java
package com.tetris.tetrisburger_backend.application.usecase.burger.admin;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.SearchMenuBurgers;
import com.tetris.tetrisburger_backend.domain.port.in.burger.query.SearchMenuBurgersQuery;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SearchMenuBurgersUseCase implements SearchMenuBurgers {

    private static final Logger logger = LoggerFactory.getLogger(SearchMenuBurgersUseCase.class);

    private final BurgerRepository burgerRepository;

    public SearchMenuBurgersUseCase(BurgerRepository burgerRepository) {
        this.burgerRepository = burgerRepository;
    }

    @Override
    public PageResponse<Burger> search(SearchMenuBurgersQuery query, PaginationRequest pagination) {
        logger.debug("Admin buscando burgers de menú por nombre: {} | page: {}, size: {}",
                query.name(), pagination.getPage(), pagination.getSize());

        PageResponse<Burger> result = burgerRepository.searchMenuByName(query.name(), pagination);

        logger.info("Encontrados {} burgers de menú", result.content().size());
        return result;
    }

}
