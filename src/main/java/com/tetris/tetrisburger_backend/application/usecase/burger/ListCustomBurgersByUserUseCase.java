// src/main/java/com/tetris/tetrisburger_backend/application/usecase/burger/ListCustomBurgersByUserUseCase.java
package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.ListCustomBurgersByUser;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ListCustomBurgersByUserUseCase implements ListCustomBurgersByUser {

    private static final Logger logger = LoggerFactory.getLogger(ListCustomBurgersByUserUseCase.class);

    private final BurgerRepository burgerRepository;

    public ListCustomBurgersByUserUseCase(BurgerRepository burgerRepository) {
        this.burgerRepository = burgerRepository;
    }

    @Override
    public PageResponse<Burger> handle(Integer idUser, PaginationRequest pagination) {
        logger.info("Listando hamburguesas custom del usuario {} page={}, size={}",
                idUser, pagination.getPage(), pagination.getSize());

        return burgerRepository.findAllCustomByUserId(idUser, pagination);
    }
}
