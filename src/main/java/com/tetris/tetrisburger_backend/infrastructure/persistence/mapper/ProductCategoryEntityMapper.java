package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.ProductCategoryEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductCategoryEntityMapper {

    default ProductCategory toDomain(ProductCategoryEntity e) {
        if (e == null) return null;
        return ProductCategory.of(e.getId(), e.getName(), e.getDescription(),
                e.getAvailable() == null ? Boolean.TRUE : e.getAvailable());
    }

    default ProductCategoryEntity toEntity(ProductCategory d) {
        if (d == null) return null;
        var e = new ProductCategoryEntity();
        e.setId(d.getId());
        e.setName(d.getName());
        e.setDescription(d.getDescription());
        e.setAvailable(d.getAvailable() == null ? Boolean.TRUE : d.getAvailable());
        return e;
    }

    default List<ProductCategory> toDomainList(List<ProductCategoryEntity> entities) {
        return entities == null ? List.of() : entities.stream().map(this::toDomain).toList();
    }

    default List<ProductCategoryEntity> toEntityList(List<ProductCategory> domains) {
        return domains == null ? List.of() : domains.stream().map(this::toEntity).toList();
    }
}