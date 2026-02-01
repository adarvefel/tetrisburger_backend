package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;

import java.util.List;
import java.util.Optional;

public interface BurgerRepository {

    Burger save(Burger burger);

    Optional<Burger> findById(Integer idBurger);

    // ========= CONSULTAS DE MENÚ =========

    /**
     * Lista todas las burgers de menú (paginado)
     */
    PageResponse<Burger> findAllOnMenu(PaginationRequest pagination);

    /**
     * Busca una burger de menú por ID
     */
    Optional<Burger> findMenuById(Integer idBurger);

    /**
     * Busca una burger de menú activa (no eliminada) por ID
     */
    Optional<Burger> findActiveMenuById(Integer idBurger);

    /**
     * Busca burgers de menú por nombre (paginado)
     */
    PageResponse<Burger> searchMenuByName(String name, PaginationRequest pagination);

    /**
     * ✅ Verifica si existe una burger de menú activa con el nombre dado
     * Solo considera burgers de menú (isOnMenu=true) que no estén eliminadas (deletedAt IS NULL)
     *
     * @param name Nombre de la burger a verificar
     * @return true si existe, false si no
     */
    boolean existsByNameAndIsOnMenuTrueAndDeletedAtIsNull(String name);

    // ========= CONSULTAS DE CUSTOM BURGERS =========

    /**
     * Lista todas las custom burgers de un usuario (paginado)
     */
    PageResponse<Burger> findAllCustomByUserId(Integer idUser, PaginationRequest pagination);

    /**
     * Busca una custom burger por ID que pertenezca al usuario
     */
    Optional<Burger> findCustomByIdAndUser(Integer idBurger, Integer idUser);

    /**
     * Busca custom burgers del usuario por nombre (paginado)
     */
    PageResponse<Burger> searchCustomByName(Integer idUser, String name, PaginationRequest pagination);
}
