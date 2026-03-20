package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.MenuEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuJpaRepository extends JpaRepository<MenuEntity, Integer> {

    @Query("SELECT m FROM MenuEntity m WHERE m.idMenu = :id AND m.deletedAt IS NULL")
    Optional<MenuEntity> findByIdMenuAndDeletedAtIsNull(@Param("id") Integer id);

    @Query("SELECT m FROM MenuEntity m WHERE m.deletedAt IS NULL")
    Page<MenuEntity> findAllByDeletedAtIsNull(Pageable pageable);

    boolean existsByIdMenu(Integer idMenu);

    @Modifying
    @Query("UPDATE MenuEntity m SET m.imageUrl = :imageUrl, m.imageKey = :imageKey, " +
            "m.updatedBy = :updatedBy WHERE m.idMenu = :idMenu")
    int updateImageFields(@Param("idMenu") Integer idMenu,
                          @Param("imageUrl") String imageUrl,
                          @Param("imageKey") String imageKey,
                          @Param("updatedBy") Integer updatedBy);

    @Query(
            value = "SELECT m.idMenu FROM MenuEntity m WHERE m.deletedAt IS NULL",
            countQuery = "SELECT COUNT(m) FROM MenuEntity m WHERE m.deletedAt IS NULL"
    )
    Page<Integer> findAllIdsByDeletedAtIsNull(Pageable pageable);

    // Query 2: carga completa con JOIN FETCH para los IDs obtenidos
    @Query("""
        SELECT DISTINCT m FROM MenuEntity m
        LEFT JOIN FETCH m.items
        LEFT JOIN FETCH m.menuCategory
        WHERE m.idMenu IN :ids
        AND m.deletedAt IS NULL
    """)
    List<MenuEntity> findAllWithRelationsByIds(@Param("ids") List<Integer> ids);



}