package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.BurgerEntity;
import jakarta.transaction.Transactional;
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
public interface BurgerJpaRepository extends JpaRepository<BurgerEntity, Integer> {

    // ========================================
    // LISTAR BURGERS DE MENÚ
    // ========================================

    /**
     * Lista todas las burgers de menú disponibles y activas
     */
    Page<BurgerEntity> findAllByIsOnMenuTrueAndAvailabilityTrueAndDeletedAtIsNull(Pageable pageable);

    /**
     * Lista todas las burgers de menú activas (sin filtrar por availability)
     */
    Page<BurgerEntity> findByIsOnMenuTrueAndDeletedAtIsNull(Pageable pageable);

    /**
     *  Lista todas las burgers de menú destacadas (favoritas)
     */
    Page<BurgerEntity> findAllByIsOnMenuTrueAndIsFavoriteTrueAndDeletedAtIsNull(Pageable pageable);

    /**
     * Lista todas las burgers de menú destacadas y disponibles
     */
    Page<BurgerEntity> findAllByIsOnMenuTrueAndIsFavoriteTrueAndAvailabilityTrueAndDeletedAtIsNull(Pageable pageable);

    // ========================================
    // LISTAR BURGERS CUSTOM
    // ========================================

    /**
     * Lista todas las burgers custom de un usuario activas
     */
    Page<BurgerEntity> findAllByIdUserAndIsCustomTrueAndDeletedAtIsNull(Integer idUser, Pageable pageable);

    Page<BurgerEntity> findAllByIdUserAndIsCustomTrueAndIsFavoriteTrueAndDeletedAtIsNull(
            Integer idUser,
            Pageable pageable
    );

    Optional<BurgerEntity> findByIdBurgerAndDeletedAtIsNull(Integer idBurger);


    List<BurgerEntity> findAllByIdUserAndIsCustomTrueAndDeletedAtIsNull(Integer idUser);

    // ========================================
    // BUSCAR BURGER POR ID
    // ========================================

    /**
     * Busca una burger custom por ID que pertenezca al usuario
     */
    Optional<BurgerEntity> findByIdBurgerAndIdUserAndIsCustomTrueAndDeletedAtIsNull(
            Integer idBurger,
            Integer idUser
    );

    /**
     * Busca una burger de menú por ID (incluye eliminadas)
     */
    Optional<BurgerEntity> findByIdBurgerAndIsOnMenuTrue(Integer idBurger);

    /**
     * Busca una burger de menú activa por ID
     */
    Optional<BurgerEntity> findByIdBurgerAndIsOnMenuTrueAndDeletedAtIsNull(Integer idBurger);

    // ========================================
    // VALIDACIÓN DE DUPLICADOS
    // ========================================

    /**
     * Verifica si existe una burger de menú activa con el nombre dado (case insensitive)
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM BurgerEntity b " +
            "WHERE LOWER(b.name) = LOWER(:name) " +
            "AND b.isOnMenu = true " +
            "AND b.deletedAt IS NULL")
    boolean existsByNameAndIsOnMenuTrueAndDeletedAtIsNull(@Param("name") String name);

    /**
     *  Verifica si existe otra burger de menú con el mismo nombre (excluyendo una específica)
     * Útil para validar UPDATE sin conflicto con el mismo registro
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM BurgerEntity b " +
            "WHERE LOWER(b.name) = LOWER(:name) " +
            "AND b.isOnMenu = true " +
            "AND b.deletedAt IS NULL " +
            "AND b.idBurger <> :excludeId")
    boolean existsByNameAndIsOnMenuTrueAndDeletedAtIsNullAndIdBurgerNot(
            @Param("name") String name,
            @Param("excludeId") Integer excludeId
    );

    /**
     * Busca una burger de menú activa por nombre exacto
     */
    @Query("SELECT b FROM BurgerEntity b " +
            "WHERE LOWER(b.name) = LOWER(:name) " +
            "AND b.isOnMenu = true " +
            "AND b.deletedAt IS NULL")
    Optional<BurgerEntity> findByNameAndIsOnMenuTrueAndDeletedAtIsNull(@Param("name") String name);

    // ========================================
    // BÚSQUEDA SIN PAGINACIÓN
    // ========================================


    /**
     *  Lista todas las burgers de menú sin paginación (para admin/reportes)
     */
    List<BurgerEntity> findAllByIsOnMenuTrueAndDeletedAtIsNull();

    // ========================================
    // BÚSQUEDA CON PAGINACIÓN
    // ========================================

    /**
     * Busca burgers de menú disponibles por nombre con paginación
     */
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

    /**
     * Busca burgers custom del usuario por nombre con paginación
     */
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

    // ========================================
    //  ESTADÍSTICAS Y CONTADORES
    // ========================================

    /**
     * Cuenta burgers de menú activas
     */
    @Query("SELECT COUNT(b) FROM BurgerEntity b " +
            "WHERE b.isOnMenu = true " +
            "AND b.deletedAt IS NULL")
    long countActiveMenuBurgers();

    /**
     * Cuenta burgers custom de un usuario
     */
    @Query("SELECT COUNT(b) FROM BurgerEntity b " +
            "WHERE b.isCustom = true " +
            "AND b.idUser = :idUser " +
            "AND b.deletedAt IS NULL")
    long countCustomBurgersByUser(@Param("idUser") Integer idUser);

    /**
     * Cuenta burgers de menú favoritas/destacadas
     */
    @Query("SELECT COUNT(b) FROM BurgerEntity b " +
            "WHERE b.isOnMenu = true " +
            "AND b.isFavorite = true " +
            "AND b.deletedAt IS NULL")
    long countFavoriteMenuBurgers();

    // =======================================
    //  BURGERS MÁS POPULARES (por timesOrdered)
    // ========================================

    /**
     * Obtiene las burgers de menú más pedidas
     */
    @Query("SELECT b FROM BurgerEntity b " +
            "WHERE b.isOnMenu = true " +
            "AND b.deletedAt IS NULL " +
            "AND b.availability = true " +
            "ORDER BY b.timesOrdered DESC, b.createdAt DESC")
    Page<BurgerEntity> findTopOrderedMenuBurgers(Pageable pageable);

    /**
     * Obtiene las burgers custom más pedidas de un usuario
     */
    @Query("SELECT b FROM BurgerEntity b " +
            "WHERE b.isCustom = true " +
            "AND b.idUser = :idUser " +
            "AND b.deletedAt IS NULL " +
            "ORDER BY b.timesOrdered DESC, b.createdAt DESC")
    Page<BurgerEntity> findTopOrderedCustomBurgersByUser(
            @Param("idUser") Integer idUser,
            Pageable pageable
    );

    // ========================================
    //  BÚSQUEDA AVANZADA
    // ========================================

    /**
     * Busca burgers de menú con filtros múltiples
     */
    @Query("SELECT b FROM BurgerEntity b " +
            "WHERE b.isOnMenu = true " +
            "AND b.deletedAt IS NULL " +
            "AND (:name IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:availability IS NULL OR b.availability = :availability) " +
            "AND (:isFavorite IS NULL OR b.isFavorite = :isFavorite) " +
            "ORDER BY b.createdAt DESC")
    Page<BurgerEntity> searchMenuBurgersWithFilters(
            @Param("name") String name,
            @Param("availability") Boolean availability,
            @Param("isFavorite") Boolean isFavorite,
            Pageable pageable
    );

    /**
     * Busca burgers custom con filtros múltiples
     */
    @Query("SELECT b FROM BurgerEntity b " +
            "WHERE b.isCustom = true " +
            "AND b.idUser = :idUser " +
            "AND b.deletedAt IS NULL " +
            "AND (:name IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:isFavorite IS NULL OR b.isFavorite = :isFavorite) " +
            "ORDER BY b.createdAt DESC")
    Page<BurgerEntity> searchCustomBurgersWithFilters(
            @Param("idUser") Integer idUser,
            @Param("name") String name,
            @Param("isFavorite") Boolean isFavorite,
            Pageable pageable
    );

    // ========================================
    //  ORDENAMIENTO POR PRECIO
    // ========================================

    /**
     * Lista burgers de menú ordenadas por precio (menor a mayor)
     */
    @Query("SELECT b FROM BurgerEntity b " +
            "WHERE b.isOnMenu = true " +
            "AND b.deletedAt IS NULL " +
            "AND b.availability = true " +
            "ORDER BY b.finalPrice ASC")
    Page<BurgerEntity> findMenuBurgersOrderByPriceAsc(Pageable pageable);

    /**
     * Lista burgers de menú ordenadas por precio (mayor a menor)
     */
    @Query("SELECT b FROM BurgerEntity b " +
            "WHERE b.isOnMenu = true " +
            "AND b.deletedAt IS NULL " +
            "AND b.availability = true " +
            "ORDER BY b.finalPrice DESC")
    Page<BurgerEntity> findMenuBurgersOrderByPriceDesc(Pageable pageable);

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
            @Param("updatedBy") Integer updatedBy
    );


    boolean existsByIdBurger(Integer idBurger);

}
