package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.MenuCategory;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.MenuCategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MenuCategoryEntityMapper {
    MenuCategory toDomain(MenuCategoryEntity entity);

    MenuCategoryEntity toEntity(MenuCategory menuCategory);
}
