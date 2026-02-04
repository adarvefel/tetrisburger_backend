package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.BurgerIngredient;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.BurgerIngredientEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class BurgerIngredientEntityMapper {

    // ==================== Entity -> Domain ====================

    public BurgerIngredient toDomain(BurgerIngredientEntity entity) {
        if (entity == null) {
            return null;
        }

        return BurgerIngredient.reconstitute(
                entity.getIdBurgerIngredient(),
                entity.getIdProduct(),
                entity.getProductName(),
                entity.getPriceAtTime(),
                entity.getQuantity(),
                entity.getSubtotal(),
                entity.getIsOptional()
        );
    }

    // ==================== Domain -> Entity ====================

    public BurgerIngredientEntity toEntity(BurgerIngredient domain) {
        if (domain == null) {
            return null;
        }

        BurgerIngredientEntity entity = new BurgerIngredientEntity();
        entity.setIdBurgerIngredient(domain.getIdBurgerIngredient());
        entity.setIdProduct(domain.getIdProduct());
        entity.setProductName(domain.getProductName());
        entity.setPriceAtTime(domain.getPriceAtTime());
        entity.setQuantity(domain.getQuantity());

        // Calcular subtotal
        BigDecimal subtotal = domain.getSubtotal();
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) == 0) {
            subtotal = domain.calculateSubtotal();
        }
        entity.setSubtotal(subtotal);

        entity.setIsOptional(domain.isOptional());

        return entity;
    }

    // ==================== Colecciones ====================

    public List<BurgerIngredient> toDomainList(List<BurgerIngredientEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }

        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    public List<BurgerIngredientEntity> toEntityList(List<BurgerIngredient> domains) {
        if (domains == null) {
            return new ArrayList<>();
        }

        return domains.stream()
                .map(this::toEntity)
                .toList();
    }
}
