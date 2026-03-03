package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.BurgerEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface BurgerJpaRepository extends JpaRepository<BurgerEntity, Integer> {

    // ========================================
    // MENÚ
    // ========================================

    Page<BurgerEntity> findAllByIsOnMenuTrueAndAvailabilityTrueAndDeletedAtIsNull(Pageable pageable);

    Page<BurgerEntity> findByIsOnMenuTrueAndDeletedAtIsNull(Pageable pageable);

    Page<BurgerEntity> findAllByIsOnMenuTrueAndIsFeaturedTrueAndDeletedAtIsNull(Pageable pageable);

    Page<BurgerEntity> findAllByIsOnMenuTrueAndIsFeaturedTrueAndAvailabilityTrueAndDeletedAtIsNull(Pageable pageable);

    // ========================================
    // CUSTOM
    // ========================================

    Page<BurgerEntity> findAllByIdUserAndIsSavedTrueAndDeletedAtIsNull(
            Integer idUser, Pageable pageable);

    Page<BurgerEntity> findAllByIdUserAndIsSavedTrueAndIsFeaturedTrueAndDeletedAtIsNull(
            Integer idUser, Pageable pageable);

    List<BurgerEntity> findAllByIdUserAndIsSavedTrueAndDeletedAtIsNull(Integer idUser);

    // ========================================
    // BUSCAR POR ID
    // ========================================

    Optional<BurgerEntity> findByIdBurgerAndDeletedAtIsNull(Integer idBurger);

    Optional<BurgerEntity> findByIdBurgerAndIdUserAndIsSavedTrueAndDeletedAtIsNull(
            Integer idBurger, Integer idUser);

    Optional<BurgerEntity> findByIdBurgerAndIsOnMenuTrue(Integer idBurger);

    Optional<BurgerEntity> findByIdBurgerAndIsOnMenuTrueAndDeletedAtIsNull(Integer idBurger);

    // ========================================
    // VALIDACIÓN DE DUPLICADOS
    // ========================================

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM BurgerEntity b " +
            "WHERE LOWER(b.name) = LOWER(:name) " +
            "AND b.isOnMenu = true " +
            "AND b.deletedAt IS NULL")
    boolean existsByNameAndIsOnMenuTrueAndDeletedAtIsNull(@Param("name") String name);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM BurgerEntity b " +
            "WHERE LOWER(b.name) = LOWER(:name) " +
            "AND b.isOnMenu = true " +
            "AND b.deletedAt IS NULL " +
            "AND b.idBurger <> :excludeId")
    boolean existsByNameAndIsOnMenuTrueAndDeletedAtIsNullAndIdBurgerNot(
            @Param("name") String name,
            @Param("excludeId") Integer excludeId);

    @Query("SELECT b FROM BurgerEntity b " +
            "WHERE LOWER(b.name) = LOWER(:name) " +
            "AND b.isOnMenu = true " +
            "AND b.deletedAt IS NULL")
    Optional<BurgerEntity> findByNameAndIsOnMenuTrueAndDeletedAtIsNull(@Param("name") String name);

    List<BurgerEntity> findAllByIsOnMenuTrueAndDeletedAtIsNull();

    // ========================================
    // BÚSQUEDA CON PAGINACIÓN
    // ========================================

    @Query("SELECT b FROM BurgerEntity b " +
            "WHERE b.isOnMenu = true " +
            "AND LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND b.deletedAt IS NULL " +
            "AND b.availability = true " +
            "ORDER BY b.createdAt DESC")
    Page<BurgerEntity> searchMenuByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT b FROM BurgerEntity b " +
            "WHERE b.isSaved = true " +
            "AND b.idUser = :idUser " +
            "AND LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND b.deletedAt IS NULL " +
            "ORDER BY b.createdAt DESC")
    Page<BurgerEntity> searchCustomByName(
            @Param("idUser") Integer idUser,
            @Param("name") String name,
            Pageable pageable);

    // ========================================
    // ESTADÍSTICAS
    // ========================================

    @Query("SELECT COUNT(b) FROM BurgerEntity b WHERE b.isOnMenu = true AND b.deletedAt IS NULL")
    long countActiveMenuBurgers();

    @Query("SELECT COUNT(b) FROM BurgerEntity b " +
            "WHERE b.isSaved = true AND b.idUser = :idUser AND b.deletedAt IS NULL")
    long countCustomBurgersByUser(@Param("idUser") Integer idUser);

    @Query("SELECT COUNT(b) FROM BurgerEntity b " +
            "WHERE b.isOnMenu = true AND b.isFeatured = true AND b.deletedAt IS NULL")
    long countFavoriteMenuBurgers();

    // ========================================
    // MÁS POPULARES
    // ========================================

    @Query("SELECT b FROM BurgerEntity b " +
            "WHERE b.isOnMenu = true AND b.deletedAt IS NULL AND b.availability = true " +
            "ORDER BY b.timesOrdered DESC, b.createdAt DESC")
    Page<BurgerEntity> findTopOrderedMenuBurgers(Pageable pageable);

    @Query("SELECT b FROM BurgerEntity b " +
            "WHERE b.isSaved = true AND b.idUser = :idUser AND b.deletedAt IS NULL " +
            "ORDER BY b.timesOrdered DESC, b.createdAt DESC")
    Page<BurgerEntity> findTopOrderedCustomBurgersByUser(
            @Param("idUser") Integer idUser, Pageable pageable);

    // ========================================
    // BÚSQUEDA AVANZADA
    // ========================================

    @Query("SELECT b FROM BurgerEntity b " +
            "WHERE b.isOnMenu = true AND b.deletedAt IS NULL " +
            "AND (:name IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:availability IS NULL OR b.availability = :availability) " +
            "AND (:isFavorite IS NULL OR b.isFeatured = :isFavorite) " +
            "ORDER BY b.createdAt DESC")
    Page<BurgerEntity> searchMenuBurgersWithFilters(
            @Param("name") String name,
            @Param("availability") Boolean availability,
            @Param("isFavorite") Boolean isFavorite,
            Pageable pageable);

    @Query("SELECT b FROM BurgerEntity b " +
            "WHERE b.isSaved = true AND b.idUser = :idUser AND b.deletedAt IS NULL " +
            "AND (:name IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:isFavorite IS NULL OR b.isFeatured = :isFavorite) " +
            "ORDER BY b.createdAt DESC")
    Page<BurgerEntity> searchCustomBurgersWithFilters(
            @Param("idUser") Integer idUser,
            @Param("name") String name,
            @Param("isFavorite") Boolean isFavorite,
            Pageable pageable);

    // ========================================
    // PRECIO
    // ========================================

    @Query("SELECT b FROM BurgerEntity b WHERE b.isOnMenu = true " +
            "AND b.deletedAt IS NULL AND b.availability = true ORDER BY b.finalPrice ASC")
    Page<BurgerEntity> findMenuBurgersOrderByPriceAsc(Pageable pageable);

    @Query("SELECT b FROM BurgerEntity b WHERE b.isOnMenu = true " +
            "AND b.deletedAt IS NULL AND b.availability = true ORDER BY b.finalPrice DESC")
    Page<BurgerEntity> findMenuBurgersOrderByPriceDesc(Pageable pageable);

    // ========================================
    // IMAGEN
    // ========================================

    @Modifying
    @Transactional
    @Query("""
        UPDATE BurgerEntity b
        SET b.imageUrl = :imageUrl,
            b.imageKey = :imageKey,
            b.updatedBy = :updatedBy,
            b.updatedAt = CURRENT_TIMESTAMP
        WHERE b.idBurger = :idBurger
        """)
    int updateImageFields(
            @Param("idBurger") Integer idBurger,
            @Param("imageUrl") String imageUrl,
            @Param("imageKey") String imageKey,
            @Param("updatedBy") Integer updatedBy);

    boolean existsByIdBurger(Integer idBurger);
}
