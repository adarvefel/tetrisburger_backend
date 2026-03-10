package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Integer>, JpaSpecificationExecutor<ProductEntity> {

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM ProductEntity p " +
            "WHERE LOWER(p.name) = LOWER(:name) AND p.deletedAt IS NULL")
    boolean existsByNameIgnoreCaseAndDeletedAtIsNull(@Param("name") String name);

    boolean existsByNameIgnoreCaseAndDeletedAtIsNullAndIdNot(String name, Integer id);

    @Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.productCategory WHERE p.id = :id")
    Optional<ProductEntity> findByIdWithCategory(@Param("id") Integer id);

    @Query("SELECT DISTINCT p FROM ProductEntity p " +
            "LEFT JOIN FETCH p.productCategory " +
            "WHERE p.deletedAt IS NULL " +
            "AND (:query IS NULL OR TRIM(:query) = '' OR " +
            "     LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "     LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:categoryId IS NULL OR p.productCategory.id = :categoryId) " +
            "AND (:availability IS NULL OR p.availability = :availability)")
    Page<ProductEntity> searchProducts(
            @Param("query") String query,
            @Param("categoryId") Integer categoryId,
            @Param("availability") Boolean availability,
            Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.deletedAt IS NULL")
    List<ProductEntity> findAllActive();

    @Query("SELECT p FROM ProductEntity p " +
            "LEFT JOIN FETCH p.productCategory " +
            "WHERE p.productType = com.tetris.tetrisburger_backend.domain.model.ProductType.INGREDIENT " +
            "AND p.deletedAt IS NULL " +
            "AND p.availability = true " +
            "AND (:categoryId IS NULL OR p.productCategory.id = :categoryId) " +
            "ORDER BY p.name ASC")
    Page<ProductEntity> findAllBurgerIngredients(
            @Param("categoryId") Integer categoryId,
            Pageable pageable);

    @Query("SELECT p FROM ProductEntity p " +
            "LEFT JOIN FETCH p.productCategory " +
            "WHERE p.productType = com.tetris.tetrisburger_backend.domain.model.ProductType.INGREDIENT " +
            "AND p.deletedAt IS NULL " +
            "AND p.availability = true " +
            "AND (:name IS NULL OR TRIM(:name) = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "ORDER BY p.name ASC")
    Page<ProductEntity> searchIngredients(
            @Param("name") String name,
            Pageable pageable);



}