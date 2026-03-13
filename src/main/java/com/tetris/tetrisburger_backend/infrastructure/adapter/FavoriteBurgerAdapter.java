package com.tetris.tetrisburger_backend.infrastructure.adapter;


import com.tetris.tetrisburger_backend.domain.model.FavoriteBurger;
import com.tetris.tetrisburger_backend.domain.port.out.FavoriteBurgerRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.FavoriteBurgerEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.BurgerJpaRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.FavoriteBurgerJpaRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FavoriteBurgerAdapter implements FavoriteBurgerRepository {

    private final FavoriteBurgerJpaRepository jpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final BurgerJpaRepository burgerJpaRepository;

    public FavoriteBurgerAdapter(FavoriteBurgerJpaRepository jpaRepository,
                                 UserJpaRepository userJpaRepository,
                                 BurgerJpaRepository burgerJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.burgerJpaRepository = burgerJpaRepository;
    }

    @Override
    public FavoriteBurger save(FavoriteBurger favorite) {
        FavoriteBurgerEntity entity = new FavoriteBurgerEntity();
        entity.setUser(userJpaRepository.getReferenceById(favorite.getIdUser()));
        entity.setBurger(burgerJpaRepository.getReferenceById(favorite.getIdBurger()));
        entity.setName(favorite.getName());
        entity.setCreatedAt(favorite.getCreatedAt());
        FavoriteBurgerEntity saved = jpaRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public void deleteByUserAndBurger(Integer idUser, Integer idBurger) {
        jpaRepository.deleteByUserIdUserAndBurgerIdBurger(idUser, idBurger);
    }

    @Override
    public boolean existsByUserAndBurger(Integer idUser, Integer idBurger) {
        return jpaRepository.existsByUserIdUserAndBurgerIdBurger(idUser, idBurger);
    }

    @Override
    public List<FavoriteBurger> findAllByUser(Integer idUser) {
        return jpaRepository.findAllByUserIdUser(idUser)
                .stream()
                .map(this::toModel)
                .toList();
    }

    // ==================== MAPPER ====================

    private FavoriteBurger toModel(FavoriteBurgerEntity entity) {
        return FavoriteBurger.reconstitute(
                entity.getIdFavorite(),
                entity.getUser().getIdUser(),
                entity.getBurger().getIdBurger(),
                entity.getName(),
                entity.getCreatedAt()
        );
    }
}
