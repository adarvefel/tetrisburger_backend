package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.MenuCategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuCategoryJpaRepository  extends JpaRepository<MenuCategoryEntity, Integer > {
    Optional<MenuCategoryEntity> findByIdMenuCategoryAndDeletedAtIsNull(Integer id);

    // Listar solo activos con paginación
    Page<MenuCategoryEntity> findAllByDeletedAtIsNull(Pageable pageable);


}
