package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.exception.BurgerNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.GetBurgerById;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class GetBurgerByIdUseCase implements GetBurgerById {

    private final BurgerRepository burgerRepository;

    public GetBurgerByIdUseCase(BurgerRepository burgerRepository) {
        this.burgerRepository = burgerRepository;
    }

    @Override
    public Burger execute(Integer idBurger) {
        Burger burger = burgerRepository.findById(idBurger)
                .orElseThrow(() -> new BurgerNotFoundException(idBurger));

        if (burger.isDeleted()) {
            throw new BurgerNotFoundException(
                    "La hamburguesa con ID " + idBurger + " ha sido eliminada"
            );
        }

        return burger;
    }
}
