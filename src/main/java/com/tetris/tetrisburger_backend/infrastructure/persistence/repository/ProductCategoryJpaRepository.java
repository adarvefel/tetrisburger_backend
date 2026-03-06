package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.ProductCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;



public interface ProductCategoryJpaRepository
        extends JpaRepository<ProductCategoryEntity, Integer>, JpaSpecificationExecutor<ProductCategoryEntity> {
    boolean existsByNameIgnoreCase(String name);
}