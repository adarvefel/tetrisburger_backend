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

    // ========================================
    // LISTAR BURGERS CUSTOM
    // ========================================

    /**
     * Lista todas las burgers custom de un usuario activas
     */
    Page<BurgerEntity> findAllByIdUserAndIsCustomTrueAndDeletedAtIsNull(Integer idUser, Pageable pageable);

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
     * ✅ Verifica si existe una burger de menú activa con el nombre dado (case insensitive)
     * Solo considera burgers de menú (isOnMenu=true) que no estén eliminadas (deletedAt IS NULL)
     *
     * @param name Nombre de la burger a verificar
     * @return true si existe, false si no
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM BurgerEntity b " +
            "WHERE LOWER(b.name) = LOWER(:name) " +
            "AND b.isOnMenu = true " +
            "AND b.deletedAt IS NULL")
    boolean existsByNameAndIsOnMenuTrueAndDeletedAtIsNull(@Param("name") String name);

    /**
     * ✅ Busca una burger de menú activa por nombre exacto (para UPDATE)
     * Útil cuando necesitas validar duplicados excluyendo la burger actual
     *
     * @param name Nombre de la burger
     * @return Optional con la burger si existe
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
     * Busca burgers de menú por nombre que contenga el texto (case insensitive)
     */
    List<BurgerEntity> findByNameContainingIgnoreCaseAndIsOnMenuTrueAndDeletedAtIsNull(String name);

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
}
