package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.ProductCategoryEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductCategoryEntityMapper {

    // ==================== Entity -> Domain ====================

    default ProductCategory toDomain(ProductCategoryEntity e) {
        if (e == null) return null;
        return ProductCategory.reconstitute(
                e.getId(),
                e.getName(),
                e.getDescription(),
                e.getAvailable() == null ? Boolean.TRUE : e.getAvailable(),
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getDeletedAt(),
                e.getCreatedBy(),
                e.getUpdatedBy(),
                e.getDeletedBy()
        );
    }

    // ==================== Domain -> Entity ====================

    default ProductCategoryEntity toEntity(ProductCategory d) {
        if (d == null) return null;
        ProductCategoryEntity e = new ProductCategoryEntity();
        e.setId(d.getId());
        e.setName(d.getName());
        e.setDescription(d.getDescription());
        e.setAvailable(d.getAvailable() == null ? Boolean.TRUE : d.getAvailable());
        e.setCreatedAt(d.getCreatedAt());
        e.setUpdatedAt(d.getUpdatedAt());
        e.setDeletedAt(d.getDeletedAt());
        e.setCreatedBy(d.getCreatedBy());
        e.setUpdatedBy(d.getUpdatedBy());
        e.setDeletedBy(d.getDeletedBy());
        return e;
    }

    // ==================== List helpers ====================

    default List<ProductCategory> toDomainList(List<ProductCategoryEntity> entities) {
        return entities == null ? List.of() : entities.stream().map(this::toDomain).toList();
    }

    default List<ProductCategoryEntity> toEntityList(List<ProductCategory> domains) {
        return domains == null ? List.of() : domains.stream().map(this::toEntity).toList();
    }
}