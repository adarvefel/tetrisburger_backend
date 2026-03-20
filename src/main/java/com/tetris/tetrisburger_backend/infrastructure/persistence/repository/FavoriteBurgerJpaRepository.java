package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.FavoriteBurgerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteBurgerJpaRepository extends JpaRepository<FavoriteBurgerEntity, Integer> {

    boolean existsByUserIdUserAndBurgerIdBurger(Integer idUser, Integer idBurger);

    void deleteByUserIdUserAndBurgerIdBurger(Integer idUser, Integer idBurger);

    List<FavoriteBurgerEntity> findAllByUserIdUser(Integer idUser);
}
