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




    // ========================================
    // MENÚ - BUSCAR
    // ========================================


    Optional<Burger> findActiveMenuById(Integer idBurger);

    Optional<Burger> findActiveById(Integer idBurger);


    // ========================================
    // MENÚ - FILTROS Y BÚSQUEDA
    // ========================================

    PageResponse<Burger> searchMenuByName(String name, PaginationRequest pagination);




    // ========================================
    // MENÚ - VALIDACIONES
    // ========================================

    boolean existsByNameAndIsOnMenuTrueAndDeletedAtIsNull(String name);



    // ========================================
    // CUSTOM (isCustom) - LISTAR
    // ========================================


    // ========================================
    // CUSTOM (isCustom) - BUSCAR
    // ========================================

    /**
     * Busca una burger personalizada por ID que pertenezca al usuario.
     * Verifica isCustom=true, idUser y deletedAt IS NULL.
     */
    Optional<Burger> findCustomByIdAndUser(Integer idBurger, Integer idUser);



    List<Burger> findAllByIngredientProductId(Integer idProduct);

    void deleteActiveDraftsByUser(Integer idUser);
    // ========================================
    // ESTADÍSTICAS
    // ========================================


    List<Burger> findAllFeaturedAndAvailable();

    /**
     * Cuenta burgers personalizadas (isCustom=true) de un usuario.
     */

    List<Burger> findAllByIds(List<Integer> ids);

}