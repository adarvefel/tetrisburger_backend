package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.BurgerIngredient;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.BurgerIngredientEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.ProductEntity;
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

        Integer productId = (entity.getProduct() != null) ? entity.getProduct().getId() : null;

        if (entity.getProduct() != null) {
            ProductEntity p = entity.getProduct();
        }

        return BurgerIngredient.reconstitute(
                entity.getIdBurgerIngredient(),
                productId,
                entity.getProductName(),
                entity.getPriceAtTime(),
                entity.getQuantity(),
                entity.getSubtotal(),
                entity.getIsOptional(),
                entity.getImageUrl()

        );
    }

    // ==================== Domain -> Entity ====================

    public BurgerIngredientEntity toEntity(BurgerIngredient domain) {
        if (domain == null) return null;

        BurgerIngredientEntity entity = new BurgerIngredientEntity();
        entity.setIdBurgerIngredient(domain.getIdBurgerIngredient());
        entity.setProductName(domain.getProductName());
        entity.setPriceAtTime(domain.getPriceAtTime());
        entity.setQuantity(domain.getQuantity());
        entity.setIsOptional(domain.getIsOptional());

        // Calcular subtotal
        BigDecimal subtotal = domain.getSubtotal();
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) == 0) {
            subtotal = domain.calculateSubtotal();
        }
        entity.setSubtotal(subtotal);

        // Asignar ProductEntity solo con ID para la relación
        if (domain.getIdProduct() != null) {
            ProductEntity product = new ProductEntity();
            product.setId(domain.getIdProduct());
            entity.setProduct(product);
        }
        entity.setImageUrl(domain.getImageUrl());

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
