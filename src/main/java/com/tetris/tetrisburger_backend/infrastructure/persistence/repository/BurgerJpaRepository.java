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
    // MENÚ - LISTAR
    // ========================================

    Page<BurgerEntity> findByIsOnMenuTrueAndDeletedAtIsNull(Pageable pageable);

    // BUSCAR POR ID (con JOIN FETCH de producto)
    // ========================================

    @Query("SELECT b FROM BurgerEntity b " +
            "LEFT JOIN FETCH b.ingredients i " +
            "LEFT JOIN FETCH i.product " +
            "WHERE b.idBurger = :idBurger AND b.deletedAt IS NULL")
    Optional<BurgerEntity> findByIdWithProductsAndDeletedAtIsNull(@Param("idBurger") Integer idBurger);


    @Query("SELECT b FROM BurgerEntity b " +
            "LEFT JOIN FETCH b.ingredients i " +
            "LEFT JOIN FETCH i.product " +
            "WHERE b.idBurger = :idBurger AND b.isOnMenu = true AND b.deletedAt IS NULL")
    Optional<BurgerEntity> findActiveMenuByIdWithProducts(@Param("idBurger") Integer idBurger);


    // ========================================
    // VALIDACIÓN DE DUPLICADOS
    // ========================================

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM BurgerEntity b " +
            "WHERE LOWER(b.name) = LOWER(:name) AND b.isOnMenu = true AND b.deletedAt IS NULL")
    boolean existsByNameAndIsOnMenuTrueAndDeletedAtIsNull(@Param("name") String name);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM BurgerEntity b " +
            "WHERE LOWER(b.name) = LOWER(:name) AND b.isOnMenu = true " +
            "AND b.deletedAt IS NULL AND b.idBurger <> :excludeId")
    boolean existsByNameAndIsOnMenuTrueAndDeletedAtIsNullAndIdBurgerNot(
            @Param("name") String name,
            @Param("excludeId") Integer excludeId);


    // ========================================
    // LISTAS POR USUARIO
    // ========================================


    // ========================================
    // BÚSQUEDA CON PAGINACIÓN
    // ========================================

    @Query("SELECT b FROM BurgerEntity b " +
            "WHERE b.isOnMenu = true " +
            "AND LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND b.deletedAt IS NULL AND b.availability = true " +
            "ORDER BY b.createdAt DESC")
    Page<BurgerEntity> searchMenuByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT b FROM BurgerEntity b " +
            "WHERE b.custom = true AND b.idUser = :idUser " +
            "AND LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND b.deletedAt IS NULL ORDER BY b.createdAt DESC")
    Page<BurgerEntity> searchCustomByName(@Param("idUser") Integer idUser,
                                          @Param("name") String name,
                                          Pageable pageable);

    // ========================================
    // ESTADÍSTICAS
    // ========================================

    @Query("SELECT COUNT(b) FROM BurgerEntity b WHERE b.isOnMenu = true AND b.deletedAt IS NULL")
    long countActiveMenuBurgers();

    @Query("SELECT COUNT(b) FROM BurgerEntity b WHERE b.custom = true AND b.idUser = :idUser AND b.deletedAt IS NULL")
    long countCustomBurgersByUser(@Param("idUser") Integer idUser);

    @Query("SELECT COUNT(b) FROM BurgerEntity b WHERE b.isOnMenu = true AND b.isFeatured = true AND b.deletedAt IS NULL")
    long countFavoriteMenuBurgers();

    // ========================================
    // MÁS POPULARES
    // ========================================


    // ========================================
    // BÚSQUEDA AVANZADA
    // ========================================



    @Modifying
    @Transactional
    @Query("""
    UPDATE BurgerEntity b 
    SET b.deletedAt = CURRENT_TIMESTAMP 
    WHERE b.idUser = :idUser 
    AND b.custom = false 
    AND b.deletedAt IS NULL
    """)
    void softDeleteAllActiveDrafts(@Param("idUser") Integer idUser);

    // ========================================
    // PRECIO
    // ========================================


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
    int updateImageFields(@Param("idBurger") Integer idBurger,
                          @Param("imageUrl") String imageUrl,
                          @Param("imageKey") String imageKey,
                          @Param("updatedBy") Integer updatedBy);

    boolean existsByIdBurger(Integer idBurger);

    @Query("SELECT b FROM BurgerEntity b WHERE b.idBurger = :idBurger " +
            "AND b.idUser = :idUser AND b.deletedAt IS NULL")
    Optional<BurgerEntity> findCustomByIdAndUser(
            @Param("idBurger") Integer idBurger,
            @Param("idUser") Integer idUser);



    @Query("""
    SELECT b FROM BurgerEntity b
    WHERE b.isFeatured = true
      AND b.availability = true
      AND b.deletedAt IS NULL
    ORDER BY b.createdAt DESC
""")
    List<BurgerEntity> findAllFeaturedAndAvailable();

    @Query("SELECT DISTINCT b FROM BurgerEntity b JOIN b.ingredients i WHERE i.product.id = :idProduct AND b.deletedAt IS NULL")
    List<BurgerEntity> findAllByIngredientProductId(@Param("idProduct") Integer idProduct);

    @Query("SELECT b FROM BurgerEntity b WHERE b.idBurger IN :ids AND b.deletedAt IS NULL")
    List<BurgerEntity> findAllByIds(@Param("ids") List<Integer> ids);


    @Modifying
    @Transactional
    @Query("""
    UPDATE BurgerEntity b 
    SET b.basePrice = (
        SELECT SUM(bi.subtotal) FROM BurgerIngredientEntity bi WHERE bi.burger.idBurger = b.idBurger
    ),
    b.updatedAt = CURRENT_TIMESTAMP
    WHERE b.idBurger IN (
        SELECT DISTINCT bi2.burger.idBurger FROM BurgerIngredientEntity bi2 WHERE bi2.product.id = :idProduct
    )
    AND b.isOnMenu = true
    AND b.deletedAt IS NULL
   """)
    void recalculateBasePriceForMenuBurgers(@Param("idProduct") Integer idProduct);

    @Modifying
    @Transactional
    @Query("""
    UPDATE BurgerEntity b 
    SET b.basePrice = (
        SELECT SUM(bi.subtotal) FROM BurgerIngredientEntity bi WHERE bi.burger.idBurger = b.idBurger
    ),
    b.finalPrice = (
        SELECT SUM(bi.subtotal) FROM BurgerIngredientEntity bi WHERE bi.burger.idBurger = b.idBurger
    ),
    b.updatedAt = CURRENT_TIMESTAMP
    WHERE b.idBurger IN (
        SELECT DISTINCT bi2.burger.idBurger FROM BurgerIngredientEntity bi2 WHERE bi2.product.id = :idProduct
    )
    AND b.isOnMenu = false
    AND b.deletedAt IS NULL
    """)
    void recalculateBasePriceForCustomBurgers(@Param("idProduct") Integer idProduct);


}