package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.BurgerIngredient;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.BurgerEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.BurgerIngredientEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BurgerIngredientEntityMapper.class})
public abstract class BurgerEntityMapper {

    @Autowired
    protected BurgerIngredientEntityMapper ingredientMapper;

    // ==================== Entity -> Domain (reconstitución del agregado) ====================

    public Burger toDomain(BurgerEntity entity) {
        if (entity == null) {
            return null;
        }

        List<BurgerIngredient> ingredients = ingredientMapper.toDomainList(entity.getIngredients());

        return Burger.reconstitute(
                entity.getIdBurger(),
                entity.getName(),
                entity.getDescription(),
                entity.getBasePrice(),
                entity.getFinalPrice(),
                entity.getMargin(),
                entity.getMarginPercentage(),
                entity.getSellingAtLoss(),
                bool(entity.getIsOnMenu()),
                bool(entity.getIsFeatured()),
                bool(entity.getIsSaved()),
                bool(entity.getAvailability()),
                entity.getImageKey(),
                entity.getImageUrl(),
                entity.getIdUser(),
                entity.getTimesOrdered(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                entity.getDeletedBy(),
                ingredients
        );
    }

    // ==================== Domain -> Entity (incluye hijos) ====================

    public BurgerEntity toEntity(Burger domain) {
        if (domain == null) {
            return null;
        }

        BurgerEntity entity = new BurgerEntity();
        entity.setIdBurger(domain.getIdBurger());          
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setBasePrice(domain.getBasePrice());
        entity.setFinalPrice(domain.getFinalPrice());
        entity.setIsOnMenu(domain.isOnMenu());
        entity.setIsSaved(domain.isSaved());
        entity.setIsFeatured(domain.isFeatured());
        entity.setAvailability(domain.isAvailability());
        entity.setImageKey(domain.getImageKey());
        entity.setImageUrl(domain.getImageUrl());
        entity.setTimesOrdered(domain.getTimesOrdered());
        entity.setIdUser(domain.getIdUser());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setDeletedAt(domain.getDeletedAt());
        entity.setCreatedBy(domain.getCreatedBy());
        entity.setUpdatedBy(domain.getUpdatedBy());
        entity.setDeletedBy(domain.getDeletedBy());

        // Mapear ingredientes
        List<BurgerIngredientEntity> childEntities =
                ingredientMapper.toEntityList(domain.getIngredients());
        entity.setIngredients(childEntities);

        // Establecer referencia bidireccional
        setParentToChildren(entity);

        return entity;
    }

    // ==================== Helper Methods ====================

    @AfterMapping
    protected void setParentToChildren(@MappingTarget BurgerEntity entity) {
        if (entity.getIngredients() != null) {
            for (BurgerIngredientEntity child : entity.getIngredients()) {
                child.setBurger(entity);
            }
        }
    }

    protected static boolean bool(Boolean value) {
        return Boolean.TRUE.equals(value);
    }

}
