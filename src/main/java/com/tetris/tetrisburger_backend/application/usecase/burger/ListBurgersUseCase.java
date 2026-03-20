package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.ListBurgers;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ListBurgersUseCase implements ListBurgers {

    private static final Logger logger = LoggerFactory.getLogger(ListBurgersUseCase.class);

    private final BurgerRepository burgerRepository;

    public ListBurgersUseCase(BurgerRepository burgerRepository) {
        this.burgerRepository = burgerRepository;
    }

    @Override
    public PageResponse<Burger> handle(PaginationRequest pagination) {
        logger.info("Listando hamburguesas del menú page={}, size={}",
                pagination.getPage(), pagination.getSize());

        return burgerRepository.findAllOnMenu(pagination);
    }
}
