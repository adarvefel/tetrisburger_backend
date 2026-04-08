package com.tetris.tetrisburger_backend.application.usecase.favoriteburger;

import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.FavoriteBurger;
import com.tetris.tetrisburger_backend.domain.model.FavoriteBurgerDetail;
import com.tetris.tetrisburger_backend.domain.port.in.favoriteburger.GetFavoriteBurgersByUser;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.FavoriteBurgerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetFavoriteBurgersByUserUseCase implements GetFavoriteBurgersByUser {

    private final FavoriteBurgerRepository favoriteBurgerRepository;
    private final BurgerRepository burgerRepository;

    public GetFavoriteBurgersByUserUseCase(FavoriteBurgerRepository favoriteBurgerRepository,
                                           BurgerRepository burgerRepository) {
        this.favoriteBurgerRepository = favoriteBurgerRepository;
        this.burgerRepository = burgerRepository;
    }

    @Override
    public List<FavoriteBurgerDetail> handle(Integer idUser) {
        List<FavoriteBurger> favorites = favoriteBurgerRepository.findAllByUser(idUser);

        return favorites.stream()
                .map(fav -> {
                    Burger burger = burgerRepository.findById(fav.getIdBurger())
                            .orElse(null);
                    return FavoriteBurgerDetail.of(fav, burger);
                })
                .filter(detail -> detail.getBurger() != null)
                .toList();
    }
}
