package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.Menu;
import com.tetris.tetrisburger_backend.domain.model.MenuItem;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.MenuEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.MenuItemEntity;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                MenuCategoryEntityMapper.class,
                BurgerEntityMapper.class,
                ProductEntityMapper.class
        }
)
public abstract class MenuEntityMapper {

    @Autowired
    protected MenuCategoryEntityMapper menuCategoryEntityMapper;

    // ── MenuEntity → Menu (dominio)
    @Mapping(target = "menuCategory", source = "menuCategory")
    @Mapping(target = "items", source = "items")
    public abstract Menu toDomain(MenuEntity entity);

    // ── MenuItem → MenuItemEntity
    @Mapping(target = "menu", ignore = true)  // se asigna manualmente abajo
    @Mapping(target = "itemType", source = "itemType")
    @Mapping(target = "burger", source = "burger")
    @Mapping(target = "product", source = "product")
    public abstract MenuItemEntity toItemEntity(MenuItem item);

    // ── MenuItemEntity → MenuItem
    @Mapping(target = "menu", ignore = true)
    @Mapping(target = "burger", source = "burger")
    @Mapping(target = "product", source = "product")
    public abstract MenuItem toItemDomain(MenuItemEntity entity);

    // ── Menu (dominio) → MenuEntity — manual para garantizar el link
    public MenuEntity toEntity(Menu menu) {
        if (menu == null) return null;

        MenuEntity entity = new MenuEntity();
        entity.setIdMenu(menu.getIdMenu());
        entity.setName(menu.getName());
        entity.setDescription(menu.getDescription());
        entity.setAvailable(menu.isAvailable());
        entity.setImageUrl(menu.getImageUrl());
        entity.setImageKey(menu.getImageKey());
        entity.setMenuCategory(
                menuCategoryEntityMapper.toEntity(menu.getMenuCategory())
        );
        entity.setCreatedAt(menu.getCreatedAt());
        entity.setUpdatedAt(menu.getUpdatedAt());
        entity.setDeletedAt(menu.getDeletedAt());
        entity.setCreatedBy(menu.getCreatedBy());
        entity.setUpdatedBy(menu.getUpdatedBy());
        entity.setDeletedBy(menu.getDeletedBy());

        List<MenuItemEntity> itemEntities = menu.getItems() != null
                ? menu.getItems().stream()
                .map(this::toItemEntity)
                .toList()
                : new ArrayList<>();

        entity.setItems(itemEntities);

        itemEntities.forEach(item -> item.setMenu(entity));

        return entity;
    }
}