package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Menu;
import com.tetris.tetrisburger_backend.domain.port.out.MenuRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.MenuEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.MenuCategoryEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.MenuEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.MenuJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MenuAdapter implements MenuRepository {

    private final MenuJpaRepository menuJpaRepository;
    private final MenuEntityMapper mapper;
    private final MenuCategoryEntityMapper categoryEntityMapper;


    public MenuAdapter(MenuJpaRepository menuJpaRepository, MenuEntityMapper mapper, MenuCategoryEntityMapper categoryEntityMapper) {
        this.menuJpaRepository = menuJpaRepository;
        this.mapper = mapper;
        this.categoryEntityMapper = categoryEntityMapper;
    }





    @Override
    public Menu save(Menu menu) {
        MenuEntity entity = mapper.toEntity(menu);
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
    public PageResponse<Menu> findAll(PaginationRequest pagination) {
        Sort sort = pagination.getSortBy() != null
                ? Sort.by(Sort.Direction.fromString(pagination.getDirection()), pagination.getSortBy())
                : Sort.unsorted();

        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize(), sort);

        Page<MenuEntity> page = menuJpaRepository.findAllByDeletedAtIsNull(pageable);

        return new PageResponse<>(
                page.getContent().stream().map(mapper::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public void delete(Menu menu) {
        MenuEntity entity = mapper.toEntity(menu);
        menuJpaRepository.save(entity);
    }



    @Override
    public Menu update(Menu menu) {
        MenuEntity entity = menuJpaRepository.findById(menu.getIdMenu())
                .orElseThrow(() -> new RuntimeException("Menú no encontrado"));

        entity.setName(menu.getName());
        entity.setDescription(menu.getDescription());
        entity.setAvailable(menu.isAvailable());
        entity.setImageUrl(menu.getImageUrl());
        entity.setImageKey(menu.getImageKey());
        entity.setMenuCategory(categoryEntityMapper.toEntity(menu.getMenuCategory()));
        entity.setUpdatedAt(menu.getUpdatedAt());
        entity.setUpdatedBy(menu.getUpdatedBy());

        entity.getItems().clear();
        menu.getItems().stream()
                .map(mapper::toItemEntity)
                .forEach(item -> {
                    item.setMenu(entity);
                    entity.getItems().add(item);
                });

        return mapper.toDomain(entity); // ← Hibernate dirty checking hace el UPDATE solo, sin save()
    }

}