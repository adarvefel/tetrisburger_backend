package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.AdditionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdditionJpaRepository extends JpaRepository<AdditionEntity, Integer> {

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT a FROM AdditionEntity a WHERE " +
            "(:available IS NULL OR a.available = :available) " +
            "AND a.deletedAt IS NULL")
    Page<AdditionEntity> findAllFiltered(
            @Param("available") Boolean available,
            Pageable pageable
    );




    @Query("SELECT a FROM AdditionEntity a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%')) AND a.deletedAt IS NULL")
    Page<AdditionEntity> findByNameContaining(@Param("name") String name, Pageable pageable);
}
