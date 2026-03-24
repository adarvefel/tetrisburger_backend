package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.domain.enums.ProductType;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.ProductCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ProductCategoryJpaRepository
        extends JpaRepository<ProductCategoryEntity, Integer>, JpaSpecificationExecutor<ProductCategoryEntity> {
    boolean existsByNameIgnoreCase(String name);


    @Query("""
    SELECT DISTINCT pc FROM ProductCategoryEntity pc
    INNER JOIN ProductEntity p ON p.productCategory.id = pc.id
    WHERE p.deletedAt IS NULL
    AND p.availability = true
    AND p.productType IN :types
    AND pc.available = true
    ORDER BY pc.name ASC
""")
    List<ProductCategoryEntity> findCategoriesWithPublicProducts(
            @Param("types") List<ProductType> types
    );
}