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

    // Entity -> Domain (reconstitución del agregado)
    public Burger toDomain(BurgerEntity e) {
        if (e == null) {
            return null;
        }

        List<BurgerIngredient> ingredients = ingredientMapper.toDomainList(e.getIngredients());

        Burger burger = Burger.reconstitute(
                e.getIdBurger(),
                safeTrim(e.getName()),
                safeTrim(e.getDescription()),
                e.getBasePrice(),
                e.getFinalPrice(),
                bool(e.getIsOnMenu()),
                bool(e.getIsFavorite()),
                bool(e.getIsCustom()),
                bool(e.getAvailability()),
                safeTrim(e.getImageUrl()),
                e.getIdUser(),
                e.getTimesOrdered(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getDeletedAt(),
                ingredients
        );

        burger.setCreatedBy(e.getCreatedBy());
        burger.setUpdatedBy(e.getUpdatedBy());
        burger.setDeletedBy(e.getDeletedBy());


        return burger;
    }



    // Domain -> Entity (incluye hijos)
    public BurgerEntity toEntity(Burger d) {
        if (d == null) {
            return null;
        }

        BurgerEntity e = new BurgerEntity();
        e.setIdBurger(d.getIdBurger());
        e.setName(d.getName());
        e.setDescription(d.getDescription());
        e.setBasePrice(d.getBasePrice());
        e.setFinalPrice(d.getFinalPrice());
        e.setIsOnMenu(d.isOnMenu());
        e.setIsCustom(d.isCustom());
        e.setIsFavorite(d.isFavorite());
        e.setAvailability(d.isAvailability());
        e.setImageUrl(d.getImageUrl());
        e.setTimesOrdered(d.getTimesOrdered());
        e.setIdUser(d.getIdUser());
        e.setCreatedAt(d.getCreatedAt());
        e.setUpdatedAt(d.getUpdatedAt());
        e.setDeletedAt(d.getDeletedAt());
        e.setCreatedBy(d.getCreatedBy());
        e.setUpdatedBy(d.getUpdatedBy());
        e.setDeletedBy(d.getDeletedBy());

        List<BurgerIngredientEntity> childEntities =
                ingredientMapper.toEntityList(d.getIngredients());
        e.setIngredients(childEntities);

        setParentToChildren(e);

        return e;
    }


    @AfterMapping
    protected void setParentToChildren(@MappingTarget BurgerEntity e) {
        if (e.getIngredients() != null) {
            for (BurgerIngredientEntity child : e.getIngredients()) {
                child.setBurger(e);
            }
        }
    }

    // Util
    protected static boolean bool(Boolean v) {
        return Boolean.TRUE.equals(v);
    }

    protected static String safeTrim(String v) {
        return v == null ? null : v.trim();
    }
}
