package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.SearchCustomBurgers;
import com.tetris.tetrisburger_backend.domain.port.in.burger.query.SearchCustomBurgersQuery;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SearchCustomBurgersByNameUseCase implements SearchCustomBurgers {

    private static final Logger logger = LoggerFactory.getLogger(SearchCustomBurgersByNameUseCase.class);

    private final BurgerRepository burgerRepository;

    public SearchCustomBurgersByNameUseCase(BurgerRepository burgerRepository) {
        this.burgerRepository = burgerRepository;
    }

    @Override
    public PageResponse<Burger> search(SearchCustomBurgersQuery query, PaginationRequest pagination) {
        logger.debug("Cliente {} buscando burgers custom por nombre: {} | page: {}, size: {}",
                query.idUser(), query.name(), pagination.getPage(), pagination.getSize());

        PageResponse<Burger> result = burgerRepository.searchCustomByName(
                query.idUser(),
                query.name(),
                pagination
        );

        logger.info("Encontrados {} burgers custom del usuario {}",
                result.content().size(), query.idUser());
        return result;
    }
}
