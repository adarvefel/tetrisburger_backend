package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface BurgerRepository {

    // ========================================
    // OPERACIONES BÁSICAS
    // ========================================

    Burger save(Burger burger);

    Optional<Burger> findById(Integer idBurger);

    void deleteById(Integer idBurger);

    boolean existsById(Integer idBurger);

    // ========================================
    // MENÚ - LISTAR
    // ========================================

    PageResponse<Burger> findAllOnMenu(PaginationRequest pagination);

    PageResponse<Burger> findAllAvailableOnMenu(PaginationRequest pagination);

    /**
     * Lista burgers destacadas del menú.
     * Filtra por isOnMenu=true, isFeatured=true, deletedAt IS NULL.
     */
    PageResponse<Burger> findAllFeaturedMenuBurgers(PaginationRequest pagination);

    List<Burger> findAllMenuBurgers();

    // ========================================
    // MENÚ - BUSCAR
    // ========================================

    Optional<Burger> findMenuById(Integer idBurger);

    Optional<Burger> findActiveMenuById(Integer idBurger);

    Optional<Burger> findActiveById(Integer idBurger);

    Optional<Burger> findByNameAndIsOnMenuTrueAndDeletedAtIsNull(String name);

    // ========================================
    // MENÚ - FILTROS Y BÚSQUEDA
    // ========================================


    PageResponse<Burger> searchMenuByName(String name, PaginationRequest pagination);

    PageResponse<Burger> searchMenuBurgersWithFilters(
            String name,
            Boolean availability,
            Boolean isFeatured,
            PaginationRequest pagination
    );


    PageResponse<Burger> findTopOrderedMenuBurgers(PaginationRequest pagination);

    PageResponse<Burger> findMenuBurgersOrderByPriceAsc(PaginationRequest pagination);

    PageResponse<Burger> findMenuBurgersOrderByPriceDesc(PaginationRequest pagination);

    // ========================================
    // MENÚ - VALIDACIONES
    // ========================================

    boolean existsByNameAndIsOnMenuTrueAndDeletedAtIsNull(String name);

    boolean existsByNameAndIsOnMenuTrueAndDeletedAtIsNullAndIdBurgerNot(
            String name,
            Integer excludeId
    );

    // ========================================
    // CUSTOM (isSaved) - LISTAR
    // ========================================

    /**
     * Lista burgers personalizadas de un usuario.
     * Filtra por idUser, isSaved=true, deletedAt IS NULL.
     */
    PageResponse<Burger> findAllSavedByUserId(Integer idUser, PaginationRequest pagination);

    /**
     * Lista burgers personalizadas destacadas de un usuario.
     * Filtra por idUser, isSaved=true, isFeatured=true, deletedAt IS NULL.
     */
    PageResponse<Burger> findAllFeaturedSavedByUserId(Integer idUser, PaginationRequest pagination);

    /**
     * Lista todas las burgers personalizadas de un usuario sin paginación.
     */
    List<Burger> findAllSavedByUserId(Integer idUser);

    // ========================================
    // CUSTOM (isSaved) - BUSCAR
    // ========================================

    /**
     * Busca una burger personalizada por ID que pertenezca al usuario.
     * Verifica isSaved=true, idUser y deletedAt IS NULL.
     */
    Optional<Burger> findSavedByIdAndUser(Integer idBurger, Integer idUser);

    PageResponse<Burger> searchSavedByName(
            Integer idUser,
            String name,
            PaginationRequest pagination
    );

    /**
     * Busca burgers personalizadas con filtros múltiples.
     * Filtra por isSaved=true, idUser, nombre e isFeatured.
     */
    PageResponse<Burger> searchSavedBurgersWithFilters(
            Integer idUser,
            String name,
            Boolean isFeatured,
            PaginationRequest pagination
    );

    PageResponse<Burger> findTopOrderedSavedBurgersByUser(
            Integer idUser,
            PaginationRequest pagination
    );

    // ========================================
    // ESTADÍSTICAS
    // ========================================

    long countActiveMenuBurgers();

    /**
     * Cuenta burgers personalizadas (isSaved=true) de un usuario.
     */
    long countSavedBurgersByUser(Integer idUser);
}
