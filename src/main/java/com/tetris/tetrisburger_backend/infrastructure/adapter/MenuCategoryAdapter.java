package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.MenuCategory;
import com.tetris.tetrisburger_backend.domain.port.out.MenuCategoryRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.MenuCategoryEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.MenuCategoryEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.MenuCategoryJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MenuCategoryAdapter implements MenuCategoryRepository {

    private final MenuCategoryJpaRepository menuCategoryJpaRepository;
    private final MenuCategoryEntityMapper entityMapper;

    public MenuCategoryAdapter(MenuCategoryJpaRepository menuCategoryJpaRepository,
                               MenuCategoryEntityMapper entityMapper) {
        this.menuCategoryJpaRepository = menuCategoryJpaRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public MenuCategory save(MenuCategory menuCategory) {
        MenuCategoryEntity entity = entityMapper.toEntity(menuCategory);
        MenuCategoryEntity saved = menuCategoryJpaRepository.save(entity);
        return entityMapper.toDomain(saved);
    }

    @Override
    public Optional<MenuCategory> findById(Integer id) {
        return menuCategoryJpaRepository
                .findByIdMenuCategoryAndDeletedAtIsNull(id)
                .map(entityMapper::toDomain);
    }

    @Override
    public PageResponse<MenuCategory> findAll(PaginationRequest pagination) {
        Sort sort = pagination.getSortBy() != null
                ? Sort.by(Sort.Direction.fromString(pagination.getDirection()), pagination.getSortBy())
                : Sort.unsorted();

        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize(), sort);

        Page<MenuCategoryEntity> page = menuCategoryJpaRepository
                .findAllByDeletedAtIsNull(pageable);

        return new PageResponse<>(
                page.getContent().stream().map(entityMapper::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public void delete(MenuCategory menuCategory) {
        MenuCategoryEntity entity = entityMapper.toEntity(menuCategory);
        menuCategoryJpaRepository.save(entity);
    }

}