package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.GetFeaturedBurgers;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetFeaturedBurgersUseCase implements GetFeaturedBurgers {

    private final BurgerRepository burgerRepository;

    public GetFeaturedBurgersUseCase(BurgerRepository burgerRepository) {
        this.burgerRepository = burgerRepository;
    }

    @Override
    public List<Burger> handle() {
        return burgerRepository.findAllFeaturedAndAvailable();
    }
}