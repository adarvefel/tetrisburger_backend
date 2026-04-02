package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Menu;
import com.tetris.tetrisburger_backend.domain.model.MenuItem;
import com.tetris.tetrisburger_backend.domain.port.out.MenuRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.BurgerEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.MenuEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.MenuItemEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.ProductEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.MenuCategoryEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.MenuEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.MenuJpaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class MenuAdapter implements MenuRepository {

    private final MenuJpaRepository menuJpaRepository;
    private final MenuEntityMapper mapper;
    private final MenuCategoryEntityMapper categoryEntityMapper;
    private final EntityManager em;

    public MenuAdapter(
            MenuJpaRepository menuJpaRepository,
            MenuEntityMapper mapper,
            MenuCategoryEntityMapper categoryEntityMapper,
            EntityManager em
    ) {
        this.menuJpaRepository = menuJpaRepository;
        this.mapper = mapper;
        this.categoryEntityMapper = categoryEntityMapper;
        this.em = em;
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private MenuItemEntity toItemEntity(MenuItem item) {
        MenuItemEntity entity = new MenuItemEntity();
        entity.setIdMenuItem(item.getIdMenuItem());
        entity.setItemType(item.getItemType());
        entity.setQuantity(item.getQuantity());

        if (item.getBurger() != null)
            entity.setBurger(em.getReference(BurgerEntity.class, item.getBurger().getIdBurger()));

        if (item.getProduct() != null)
            entity.setProduct(em.getReference(ProductEntity.class, item.getProduct().getId()));

        return entity;
    }

    // ── Puerto ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public Menu save(Menu menu) {
        MenuEntity entity = mapper.toEntity(menu);

        if (entity.getItems() != null) {
            entity.getItems().forEach(item -> {
                if (item.getBurger() != null)
                    item.setBurger(em.getReference(BurgerEntity.class, item.getBurger().getIdBurger()));
                if (item.getProduct() != null)
                    item.setProduct(em.getReference(ProductEntity.class, item.getProduct().getId()));
            });
        }

        MenuEntity saved = menuJpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Menu> findById(Integer id) {
        return menuJpaRepository
                .findByIdMenuAndDeletedAtIsNull(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Menu> findAll(PaginationRequest pagination) {
        Sort sort = pagination.getSortBy() != null
                ? Sort.by(Sort.Direction.fromString(pagination.getDirection()), pagination.getSortBy())
                : Sort.unsorted();

        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize(), sort);

        Page<Integer> idPage = menuJpaRepository.findAllIdsByDeletedAtIsNull(pageable);

        if (idPage.isEmpty()) {
            return new PageResponse<>(
                    List.of(),
                    idPage.getNumber(),
                    idPage.getSize(),
                    0L,
                    0
            );
        }

        List<MenuEntity> entities = menuJpaRepository.findAllWithRelationsByIds(idPage.getContent());

        Map<Integer, MenuEntity> entityMap = entities.stream()
                .collect(Collectors.toMap(MenuEntity::getIdMenu, Function.identity()));

        List<Menu> menus = idPage.getContent().stream()
                .map(entityMap::get)
                .filter(Objects::nonNull)
                .map(mapper::toDomain)
                .toList();

        return new PageResponse<>(
                menus,
                idPage.getNumber(),
                idPage.getSize(),
                idPage.getTotalElements(),
                idPage.getTotalPages()
        );
    }

    @Override
    @Transactional
    public void delete(Menu menu) {
        MenuEntity entity = mapper.toEntity(menu);
        menuJpaRepository.save(entity);
    }

    @Override
    @Transactional
    public Menu update(Menu menu) {
        MenuEntity entity = menuJpaRepository.findById(menu.getIdMenu())
                .orElseThrow(() -> new RuntimeException("Menú no encontrado"));

        // Actualizar campos del menú
        entity.setName(menu.getName());
        entity.setDescription(menu.getDescription());
        entity.setAvailable(menu.isAvailable());
        entity.setImageUrl(menu.getImageUrl());
        entity.setImageKey(menu.getImageKey());
        entity.setMenuCategory(categoryEntityMapper.toEntity(menu.getMenuCategory()));
        entity.setUpdatedAt(menu.getUpdatedAt());
        entity.setUpdatedBy(menu.getUpdatedBy());

        // Construir los nuevos ítems con proxies JPA
        List<MenuItemEntity> newItems = menu.getItems().stream()
                .map(this::toItemEntity)
                .toList();

        // IDs que deben quedar
        List<Integer> newIds = newItems.stream()
                .map(MenuItemEntity::getIdMenuItem)
                .filter(Objects::nonNull)
                .toList();

        // Eliminar solo los que ya no están
        entity.getItems().removeIf(existing ->
                existing.getIdMenuItem() == null ||
                        !newIds.contains(existing.getIdMenuItem())
        );

        // Actualizar existentes o agregar nuevos
        newItems.forEach(newItem -> {
            if (newItem.getIdMenuItem() == null) {
                // Ítem nuevo → agregar con link al menú padre
                newItem.setMenu(entity);
                entity.getItems().add(newItem);
            } else {
                // Ítem existente → actualizar solo sus campos
                entity.getItems().stream()
                        .filter(e -> e.getIdMenuItem().equals(newItem.getIdMenuItem()))
                        .findFirst()
                        .ifPresent(existing -> {
                            existing.setItemType(newItem.getItemType());
                            existing.setBurger(newItem.getBurger());
                            existing.setProduct(newItem.getProduct());
                            existing.setQuantity(newItem.getQuantity());
                        });
            }
        });

        MenuEntity saved = menuJpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}