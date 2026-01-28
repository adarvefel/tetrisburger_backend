// src/main/java/com/tetris/tetrisburger_backend/infrastructure/persistence/mapper/ProductEntityMapper.java
package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {

    // Entity -> Domain
    default Product toDomain(ProductEntity e) {
        if (e == null) return null;
        return Product.of(
                e.getId(),
                trim(e.getName()),
                trim(e.getDescription()),
                e.getQuantity(),
                e.getPrice(),
                e.getAvailability(),
                trim(e.getProductType()),
                trim(e.getIngredientType()),
                e.getBurgerIngredient(),
                trim(e.getImageUrl()),      // ← Nombre original
                trim(e.getImageKey()),      // ← Key S3 (AGREGADO)
                e.getProductCategoryId(),
                e.getSupplierId(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getDeletedAt(),
                e.getCreatedBy(),
                e.getUpdatedBy(),
                e.getDeletedBy()
        );
    }

    // Domain -> Entity
    default ProductEntity toEntity(Product d) {
        if (d == null) return null;
        ProductEntity e = new ProductEntity();
        e.setId(d.getId());
        e.setName(trim(d.getName()));
        e.setDescription(trim(d.getDescription()));
        e.setQuantity(d.getQuantity());
        e.setPrice(d.getPrice());
        e.setAvailability(d.getAvailability());
        e.setProductType(trim(d.getProductType()));
        e.setIngredientType(trim(d.getIngredientType()));
        e.setBurgerIngredient(d.getBurgerIngredient());
        e.setImageUrl(trim(d.getImageUrl()));      // ← Nombre original
        e.setImageKey(trim(d.getImageKey()));      // ← Key S3 (AGREGADO)
        e.setProductCategoryId(d.getProductCategoryId());
        e.setSupplierId(d.getSupplierId());
        e.setCreatedAt(d.getCreatedAt());
        e.setUpdatedAt(d.getUpdatedAt());
        e.setDeletedAt(d.getDeletedAt());
        e.setCreatedBy(d.getCreatedBy());
        e.setUpdatedBy(d.getUpdatedBy());
        e.setDeletedBy(d.getDeletedBy());
        return e;
    }

    // List helpers
    default List<Product> toDomainList(List<ProductEntity> entities) {
        return entities == null ? List.of() : entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    default List<ProductEntity> toEntityList(List<Product> domains) {
        return domains == null ? List.of() : domains.stream().map(this::toEntity).collect(Collectors.toList());
    }

    // trim util
    default String trim(String s) {
        return s == null ? null : s.trim();
    }
}
