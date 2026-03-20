package com.tetris.tetrisburger_backend.application.usecase.favoriteburger;

import com.tetris.tetrisburger_backend.domain.exception.FavoriteNotFoundException;
import com.tetris.tetrisburger_backend.domain.port.in.favoriteburger.RemoveFavoriteBurger;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.FavoriteBurgerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
@Service
@Transactional
public class RemoveFavoriteBurgerUseCase implements RemoveFavoriteBurger {

    private final FavoriteBurgerRepository favoriteBurgerRepository;
    private final BurgerRepository burgerRepository;

    public RemoveFavoriteBurgerUseCase(FavoriteBurgerRepository favoriteBurgerRepository,
                                       BurgerRepository burgerRepository) {
        this.favoriteBurgerRepository = favoriteBurgerRepository;
        this.burgerRepository = burgerRepository;
    }

    @Override
    public void handle(Integer idUser, Integer idBurger) {
        if (!favoriteBurgerRepository.existsByUserAndBurger(idUser, idBurger))
            throw new FavoriteNotFoundException("El usuario " + idUser + " no tiene en favoritos la hamburguesa " + idBurger);

        // Si es custom burger del usuario → soft delete
        burgerRepository.findById(idBurger).ifPresent(burger -> {
            if (!burger.isOnMenu() && burger.getIdUser() != null
                    && burger.getIdUser().equals(idUser)) {
                burger.markAsDeleted(idUser);
                burgerRepository.save(burger);
            }
        });

        favoriteBurgerRepository.deleteByUserAndBurger(idUser, idBurger);
    }
}