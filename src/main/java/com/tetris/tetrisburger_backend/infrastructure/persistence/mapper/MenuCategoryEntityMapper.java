package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.MenuCategory;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.MenuCategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;



@Mapper(componentModel = "spring")
public interface MenuCategoryEntityMapper {

    default MenuCategoryEntity toEntity(MenuCategory domain) {
        if (domain == null) return null;
        return MenuCategoryEntity.builder()
                .idMenuCategory(domain.getIdMenuCategory())
                .menuCategoryName(domain.getMenuCategoryName())
                .description(domain.getDescription())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .deletedAt(domain.getDeletedAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .deletedBy(domain.getDeletedBy())
                .build();
    }

    default MenuCategory toDomain(MenuCategoryEntity entity) {
        if (entity == null) return null;
        return MenuCategory.reconstitute(
                entity.getIdMenuCategory(),
                entity.getMenuCategoryName(),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                entity.getDeletedBy()
        );
    }
}