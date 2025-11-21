package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.BurgerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BurgerJpaRepository extends JpaRepository<BurgerEntity, Integer> {

    // ========= LISTAR =========
    // Burgers de menú (sin usuario, con pageable)
    Page<BurgerEntity> findAllByIsOnMenuTrueAndAvailabilityTrueAndDeletedAtIsNull(Pageable pageable);

    // Burgers custom de un usuario (con usuario, con pageable)
    Page<BurgerEntity> findAllByIdUserAndIsCustomTrueAndDeletedAtIsNull(Integer idUser, Pageable pageable);

    // ========= BUSCAR POR ID =========
    Optional<BurgerEntity> findByIdBurgerAndIdUserAndIsCustomTrueAndDeletedAtIsNull(Integer idBurger, Integer idUser);

    Optional<BurgerEntity> findByIdBurgerAndIsOnMenuTrue(Integer idBurger);

    Optional<BurgerEntity> findByIdBurgerAndIsOnMenuTrueAndDeletedAtIsNull(Integer idBurger);

    // ========= BÚSQUEDA SIN PAGINACIÓN =========
    List<BurgerEntity> findByNameContainingIgnoreCaseAndIsOnMenuTrueAndDeletedAtIsNull(String name);

    // ========= BÚSQUEDA CON PAGINACIÓN =========
    @Query("SELECT b FROM BurgerEntity b " +
            "WHERE b.isOnMenu = true " +
            "AND LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND b.deletedAt IS NULL " +
            "AND b.availability = true " +
            "ORDER BY b.createdAt DESC")
    Page<BurgerEntity> searchMenuByName(
            @Param("name") String name,
            Pageable pageable
    );

    @Query("SELECT b FROM BurgerEntity b " +
            "WHERE b.isCustom = true " +
            "AND b.idUser = :idUser " +
            "AND LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND b.deletedAt IS NULL " +
            "ORDER BY b.createdAt DESC")
    Page<BurgerEntity> searchCustomByName(
            @Param("idUser") Integer idUser,
            @Param("name") String name,
            Pageable pageable
    );
}
