package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Integer>, JpaSpecificationExecutor<ProductEntity> {
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM ProductEntity p " +
            "WHERE LOWER(p.name) = LOWER(:name) AND p.deletedAt IS NULL")
    boolean existsByNameIgnoreCaseAndDeletedAtIsNull(@Param("name") String name);

}