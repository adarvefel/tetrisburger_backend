// src/main/java/com/tetris/tetrisburger_backend/infrastructure/persistence/mapper/BurgerIngredientEntityMapper.java
package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.BurgerIngredient;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.BurgerIngredientEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BurgerIngredientEntityMapper {

    // Entity -> Domain
    public BurgerIngredient toDomain(BurgerIngredientEntity e) {
        if (e == null) {
            return null;
        }
        return BurgerIngredient.reconstitute(
                e.getIdBurgerIngredient(),
                e.getIdProduct(),
                e.getPriceAtTime(),
                e.getQuantity(),
                e.getIsOptional()
        );
    }

    // Domain -> Entity
    public BurgerIngredientEntity toEntity(BurgerIngredient d) {
        if (d == null) {
            return null;
        }
        BurgerIngredientEntity e = new BurgerIngredientEntity();
        e.setIdBurgerIngredient(d.getIdBurgerIngredient());
        e.setIdProduct(d.getIdProduct());
        e.setPriceAtTime(d.getPriceAtTime());
        e.setQuantity(d.getQuantity());
        e.setIsOptional(d.getIsOptional());
        return e;
    }

    // Colecciones
    public List<BurgerIngredient> toDomainList(List<BurgerIngredientEntity> entities) {
        return entities == null ? List.of() : entities.stream()
                .map(this::toDomain)
                .toList();
    }

    public List<BurgerIngredientEntity> toEntityList(List<BurgerIngredient> domains) {
        return domains == null ? List.of() : domains.stream()
                .map(this::toEntity)
                .toList();
    }
}
