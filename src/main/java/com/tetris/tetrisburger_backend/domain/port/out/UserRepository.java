package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.ListUsersQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de usuarios.
 * Define las operaciones que el dominio necesita para interactuar con la capa de persistencia.
 */
public interface UserRepository {

    /**
     * Guardar o actualizar un usuario
     */
    User saveUser(User user);

    /**
     * Buscar usuario por ID
     */
    Optional<User> findUserById(Integer id);

    /**
     * Buscar usuario por email
     */
    PageResponse<User> findUserByEmail(String email, PaginationRequest request);

    boolean existsByEmail(String email);

    boolean existsById(Integer id);




    Optional<User> findUserByEmail(String email);

    /**
     * Buscar todos los usuarios con paginación
     */
    PageResponse<User> findAllUsers(ListUsersQuery query);

    void softDeleteUser(Integer idUser, Integer deletedBy);

    /**
     * Eliminar usuario por ID
     */

    void deleteUserById(Integer idUser);



}