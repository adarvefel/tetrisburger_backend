package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;

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
    // CUSTOM (isCustom) - LISTAR
    // ========================================

    /**
     * Lista burgers personalizadas de un usuario.
     * Filtra por idUser, isCustom=true, deletedAt IS NULL.
     */
    PageResponse<Burger> findAllCustomByUserId(Integer idUser, PaginationRequest pagination);


    /**
     * Lista todas las burgers personalizadas de un usuario sin paginación.
     */
    List<Burger> findAllCustomByUserId(Integer idUser);

    // ========================================
    // CUSTOM (isCustom) - BUSCAR
    // ========================================

    /**
     * Busca una burger personalizada por ID que pertenezca al usuario.
     * Verifica isCustom=true, idUser y deletedAt IS NULL.
     */
    Optional<Burger> findCustomByIdAndUser(Integer idBurger, Integer idUser);

    /**
     * Busca burgers personalizadas por nombre.
     */
    PageResponse<Burger> searchCustomByName(
            Integer idUser,
            String name,
            PaginationRequest pagination
    );

    /**
     * Busca burgers personalizadas con filtros múltiples.
     * Filtra por isCustom=true, idUser, nombre e isFeatured.
     */
    PageResponse<Burger> searchCustomBurgersWithFilters(
            Integer idUser,
            String name,
            Boolean isFeatured,
            PaginationRequest pagination
    );

    PageResponse<Burger> findTopOrderedCustomBurgersByUser(
            Integer idUser,
            PaginationRequest pagination
    );

    void deleteActiveDraftsByUser(Integer idUser);
    // ========================================
    // ESTADÍSTICAS
    // ========================================

    long countActiveMenuBurgers();

    /**
     * Cuenta burgers personalizadas (isCustom=true) de un usuario.
     */
    long countCustomBurgersByUser(Integer idUser);
}