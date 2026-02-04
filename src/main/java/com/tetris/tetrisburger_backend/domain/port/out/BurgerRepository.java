package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para operaciones de persistencia de Burger.
 * Define el contrato para acceso a datos siguiendo arquitectura hexagonal.
 *
 * @see Burger
 * @see PageResponse
 * @see PaginationRequest
 */
public interface BurgerRepository {

    // ========================================
    // OPERACIONES BÁSICAS
    // ========================================

    /**
     * Guarda o actualiza una hamburguesa.
     *
     * @param burger Hamburguesa a guardar
     * @return Hamburguesa guardada con ID asignado
     */
    Burger save(Burger burger);

    /**
     * Busca una hamburguesa por su ID.
     *
     * @param idBurger ID de la hamburguesa
     * @return Optional con la hamburguesa si existe
     */
    Optional<Burger> findById(Integer idBurger);

    /**
     * Elimina una hamburguesa por su ID.
     *
     * @param idBurger ID de la hamburguesa a eliminar
     */
    void deleteById(Integer idBurger);

    /**
     * Verifica si existe una hamburguesa con el ID dado.
     *
     * @param idBurger ID de la hamburguesa
     * @return true si existe, false si no
     */
    boolean existsById(Integer idBurger);

    // ========================================
    // CONSULTAS DE MENÚ - LISTAR
    // ========================================

    /**
     * Lista todas las hamburguesas del menú activas (no eliminadas).
     *
     * @param pagination Configuración de paginación
     * @return Página con hamburguesas del menú
     */
    PageResponse<Burger> findAllOnMenu(PaginationRequest pagination);

    /**
     * Lista todas las hamburguesas del menú disponibles para venta.
     * Filtra por isOnMenu=true, availability=true y deletedAt IS NULL.
     *
     * @param pagination Configuración de paginación
     * @return Página con hamburguesas disponibles
     */
    PageResponse<Burger> findAllAvailableOnMenu(PaginationRequest pagination);

    /**
     * Lista todas las hamburguesas destacadas del menú.
     * Filtra por isOnMenu=true, isFavorite=true y deletedAt IS NULL.
     *
     * @param pagination Configuración de paginación
     * @return Página con hamburguesas destacadas
     */
    PageResponse<Burger> findAllFavoriteMenuBurgers(PaginationRequest pagination);

    /**
     * Lista todas las hamburguesas del menú sin paginación.
     * Útil para exportaciones o listados completos.
     *
     * @return Lista completa de hamburguesas del menú
     */
    List<Burger> findAllMenuBurgers();

    // ========================================
    // CONSULTAS DE MENÚ - BUSCAR
    // ========================================

    /**
     * Busca una hamburguesa de menú por ID (incluye eliminadas).
     *
     * @param idBurger ID de la hamburguesa
     * @return Optional con la hamburguesa si es de menú
     */
    Optional<Burger> findMenuById(Integer idBurger);

    /**
     * Busca una hamburguesa de menú activa por ID.
     * Solo retorna si isOnMenu=true y deletedAt IS NULL.
     *
     * @param idBurger ID de la hamburguesa
     * @return Optional con la hamburguesa si está activa
     */
    Optional<Burger> findActiveMenuById(Integer idBurger);

    /**
     * Busca una hamburguesa de menú activa por nombre exacto.
     * Útil para validar duplicados.
     *
     * @param name Nombre exacto de la hamburguesa
     * @return Optional con la hamburguesa si existe
     */
    Optional<Burger> findByNameAndIsOnMenuTrueAndDeletedAtIsNull(String name);

    // ========================================
    // CONSULTAS DE MENÚ - BÚSQUEDA Y FILTROS
    // ========================================

    /**
     * Busca hamburguesas de menú por nombre (búsqueda parcial).
     * Filtra por isOnMenu=true y deletedAt IS NULL.
     *
     * @param name Texto a buscar en el nombre
     * @param pagination Configuración de paginación
     * @return Página con resultados de búsqueda
     */
    PageResponse<Burger> searchMenuByName(String name, PaginationRequest pagination);

    /**
     * Busca hamburguesas de menú con filtros múltiples.
     * Permite combinar filtros de nombre, disponibilidad y destacadas.
     *
     * @param name Nombre a buscar (opcional)
     * @param availability Filtro de disponibilidad (opcional)
     * @param isFavorite Filtro de destacadas (opcional)
     * @param pagination Configuración de paginación
     * @return Página con resultados filtrados
     */
    PageResponse<Burger> searchMenuBurgersWithFilters(
            String name,
            Boolean availability,
            Boolean isFavorite,
            PaginationRequest pagination
    );

    /**
     * Lista las hamburguesas de menú más pedidas.
     * Ordenadas por timesOrdered descendente.
     *
     * @param pagination Configuración de paginación
     * @return Página con hamburguesas más populares
     */
    PageResponse<Burger> findTopOrderedMenuBurgers(PaginationRequest pagination);

    /**
     * Lista hamburguesas de menú ordenadas por precio ascendente.
     *
     * @param pagination Configuración de paginación
     * @return Página ordenada por precio de menor a mayor
     */
    PageResponse<Burger> findMenuBurgersOrderByPriceAsc(PaginationRequest pagination);

    /**
     * Lista hamburguesas de menú ordenadas por precio descendente.
     *
     * @param pagination Configuración de paginación
     * @return Página ordenada por precio de mayor a menor
     */
    PageResponse<Burger> findMenuBurgersOrderByPriceDesc(PaginationRequest pagination);

    // ========================================
    // CONSULTAS DE MENÚ - VALIDACIONES
    // ========================================

    /**
     * Verifica si existe una hamburguesa de menú activa con el nombre dado.
     * Solo considera isOnMenu=true y deletedAt IS NULL.
     *
     * @param name Nombre de la hamburguesa a verificar
     * @return true si existe, false si no
     */
    boolean existsByNameAndIsOnMenuTrueAndDeletedAtIsNull(String name);

    /**
     * Verifica si existe otra hamburguesa de menú activa con el mismo nombre.
     * Útil para validar duplicados al actualizar (excluye el ID actual).
     *
     * @param name Nombre a verificar
     * @param excludeId ID de la hamburguesa a excluir de la búsqueda
     * @return true si existe otro registro con ese nombre
     */
    boolean existsByNameAndIsOnMenuTrueAndDeletedAtIsNullAndIdBurgerNot(
            String name,
            Integer excludeId
    );

    // ========================================
    // CONSULTAS DE CUSTOM BURGERS - LISTAR
    // ========================================

    /**
     * Lista todas las hamburguesas personalizadas de un usuario.
     * Filtra por idUser, isCustom=true y deletedAt IS NULL.
     *
     * @param idUser ID del usuario
     * @param pagination Configuración de paginación
     * @return Página con hamburguesas personalizadas del usuario
     */
    PageResponse<Burger> findAllCustomByUserId(Integer idUser, PaginationRequest pagination);

    /**
     * Lista las hamburguesas personalizadas favoritas de un usuario.
     * Filtra por idUser, isCustom=true, isFavorite=true y deletedAt IS NULL.
     *
     * @param idUser ID del usuario
     * @param pagination Configuración de paginación
     * @return Página con hamburguesas favoritas del usuario
     */
    PageResponse<Burger> findAllFavoriteCustomByUserId(Integer idUser, PaginationRequest pagination);

    /**
     * Lista todas las hamburguesas personalizadas de un usuario sin paginación.
     * Útil para exportaciones o listados completos.
     *
     * @param idUser ID del usuario
     * @return Lista completa de hamburguesas personalizadas
     */
    List<Burger> findAllCustomByUserId(Integer idUser);

    // ========================================
    // CONSULTAS DE CUSTOM BURGERS - BUSCAR
    // ========================================

    /**
     * Busca una hamburguesa personalizada por ID que pertenezca al usuario.
     * Verifica que sea del usuario y esté activa.
     *
     * @param idBurger ID de la hamburguesa
     * @param idUser ID del usuario propietario
     * @return Optional con la hamburguesa si es del usuario
     */
    Optional<Burger> findCustomByIdAndUser(Integer idBurger, Integer idUser);

    /**
     * Busca hamburguesas personalizadas del usuario por nombre.
     *
     * @param idUser ID del usuario
     * @param name Texto a buscar en el nombre
     * @param pagination Configuración de paginación
     * @return Página con resultados de búsqueda
     */
    PageResponse<Burger> searchCustomByName(
            Integer idUser,
            String name,
            PaginationRequest pagination
    );

    /**
     * Busca hamburguesas personalizadas con filtros múltiples.
     * Permite combinar filtros de nombre y favoritas.
     *
     * @param idUser ID del usuario
     * @param name Nombre a buscar (opcional)
     * @param isFavorite Filtro de favoritas (opcional)
     * @param pagination Configuración de paginación
     * @return Página con resultados filtrados
     */
    PageResponse<Burger> searchCustomBurgersWithFilters(
            Integer idUser,
            String name,
            Boolean isFavorite,
            PaginationRequest pagination
    );

    /**
     * Lista las hamburguesas personalizadas más pedidas del usuario.
     * Ordenadas por timesOrdered descendente.
     *
     * @param idUser ID del usuario
     * @param pagination Configuración de paginación
     * @return Página con hamburguesas más pedidas del usuario
     */
    PageResponse<Burger> findTopOrderedCustomBurgersByUser(
            Integer idUser,
            PaginationRequest pagination
    );

    // ========================================
    // ESTADÍSTICAS
    // ========================================

    /**
     * Cuenta las hamburguesas activas del menú.
     *
     * @return Número total de hamburguesas de menú activas
     */
    long countActiveMenuBurgers();

    /**
     * Cuenta las hamburguesas personalizadas de un usuario.
     *
     * @param idUser ID del usuario
     * @return Número total de hamburguesas personalizadas del usuario
     */
    long countCustomBurgersByUser(Integer idUser);

    /**
     * Cuenta las hamburguesas destacadas del menú.
     *
     * @return Número total de hamburguesas marcadas como favoritas
     */
    long countFavoriteMenuBurgers();


    Optional<Burger> findActiveById(Integer idBurger);

}
