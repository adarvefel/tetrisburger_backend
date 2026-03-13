package com.tetris.tetrisburger_backend.application.usecase.favoriteburger;


import com.tetris.tetrisburger_backend.domain.exception.EntityNotFoundException;
import com.tetris.tetrisburger_backend.domain.exception.FavoriteAlreadyExistsException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidBurgerException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.FavoriteBurger;
import com.tetris.tetrisburger_backend.domain.port.in.favoriteburger.AddFavoriteBurger;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.FavoriteBurgerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AddFavoriteBurgerUseCase implements AddFavoriteBurger {

    private final FavoriteBurgerRepository favoriteBurgerRepository;
    private final BurgerRepository burgerRepository;

    public AddFavoriteBurgerUseCase(FavoriteBurgerRepository favoriteBurgerRepository,
                                    BurgerRepository burgerRepository) {
        this.favoriteBurgerRepository = favoriteBurgerRepository;
        this.burgerRepository = burgerRepository;
    }

    @Override
    public FavoriteBurger handle(Integer idUser, Integer idBurger) {
        if (favoriteBurgerRepository.existsByUserAndBurger(idUser, idBurger))
            throw new FavoriteAlreadyExistsException("El usuario " + idUser + " ya tiene en favoritos la hamburguesa " + idBurger);

        Burger burger = burgerRepository.findById(idBurger)
                .orElseThrow(() -> new EntityNotFoundException("Hamburguesa no encontrada: " + idBurger));

        // Validar que no sea custom de otro usuario
        if (!burger.isOnMenu() && burger.getIdUser() != null
                && !burger.getIdUser().equals(idUser))
            throw new InvalidBurgerException(
                    "No puedes agregar como favorita una hamburguesa personalizada de otro usuario");

        // Si es draft del usuario → convertir a custom
        if (!burger.isOnMenu() && burger.getIdUser() != null
                && burger.getIdUser().equals(idUser) && !burger.isCustom()) {
            burger.saveAsCustom(idUser);
            burgerRepository.save(burger);
        }

        FavoriteBurger favorite = FavoriteBurger.create(idUser, idBurger, burger.getName());
        return favoriteBurgerRepository.save(favorite);
    }
}

