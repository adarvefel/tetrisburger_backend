package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;


import com.tetris.tetrisburger_backend.domain.model.FavoriteBurger;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.BurgerEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.FavoriteBurgerEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FavoriteBurgerEntityMapper {

    // Entity → Model
    public FavoriteBurger toModel(FavoriteBurgerEntity entity) {
        return FavoriteBurger.reconstitute(
                entity.getIdFavorite(),
                entity.getUser().getIdUser(),
                entity.getBurger().getIdBurger(),
                entity.getName(),
                entity.getCreatedAt()
        );
    }

    // Model → Entity
    public FavoriteBurgerEntity toEntity(FavoriteBurger model,
                                         UserEntity user,
                                         BurgerEntity burger) {
        FavoriteBurgerEntity entity = new FavoriteBurgerEntity();
        entity.setUser(user);
        entity.setBurger(burger);
        entity.setName(model.getName());
        entity.setCreatedAt(model.getCreatedAt());
        return entity;
    }

    public List<FavoriteBurger> toModelList(List<FavoriteBurgerEntity> entities) {
        return entities.stream()
                .map(this::toModel)
                .toList();
    }
}
