package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.model.MenuCategory;
import com.tetris.tetrisburger_backend.domain.port.out.MenuCategoryRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.MenuCategoryEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.MenuCategoryEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.MenuCategoryJpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public class MenuCategoryAdapter  implements MenuCategoryRepository {

    private final MenuCategoryJpaRepository menuCategoryJpaRepository;
    private final MenuCategoryEntityMapper entityMapper;

    public MenuCategoryAdapter(MenuCategoryJpaRepository menuCategoryJpaRepository, MenuCategoryEntityMapper entityMapper) {
        this.menuCategoryJpaRepository = menuCategoryJpaRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public MenuCategory save(MenuCategory menuCategory) {
        MenuCategoryEntity entity = entityMapper.toEntity(menuCategory);

        MenuCategoryEntity savedEntity = menuCategoryJpaRepository.save(entity);

        return entityMapper.toDomain(savedEntity);
    }
}
