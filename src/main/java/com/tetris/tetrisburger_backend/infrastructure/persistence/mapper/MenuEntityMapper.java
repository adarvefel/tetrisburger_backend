package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.Menu;
import com.tetris.tetrisburger_backend.domain.model.MenuItem;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.MenuEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.MenuItemEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MenuEntityMapper {

    @Mapping(target = "items", source = "items")
    Menu toDomain(MenuEntity entity);

    @Mapping(target = "items", source = "items")
    MenuEntity toEntity(Menu menu);

    @Mapping(target = "menu", ignore = true)
    MenuItemEntity toItemEntity(MenuItem item);

    @Mapping(target = "idMenu", source = "menu.idMenu")
    MenuItem toItemDomain(MenuItemEntity entity);

    @AfterMapping
    default void linkItems(@MappingTarget MenuEntity menuEntity) {
        if (menuEntity.getItems() != null) {
            menuEntity.getItems().forEach(item -> item.setMenu(menuEntity));
        }
    }
}