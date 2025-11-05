package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.*;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.CreateUserByAdminCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.DeleteUserByAdminCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateUserByAdminCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.ListUsersQuery;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.user.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.UserRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para gestión de usuarios por ADMIN
 * Sigue Clean Architecture: coordina entre REST y casos de uso
 */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")  // ✅ TODOS los métodos requieren ADMIN
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // Ports IN (Use Cases)
    private final CreateUserByAdmin createUserByAdmin;
    private final ListUser listUser;
    private final GetUserById getUserById;
    private final UpdateUserByAdmin updateUserByAdmin;
    private final DeleteUserByAdmin deleteUserByAdmin;

    // Mapper
    private final UserRestDtoMapper mapper;

    // ✅ Constructor limpio (sin duplicados)
    public UserController(
            CreateUserByAdmin createUserByAdmin,
            ListUser listUser,
            GetUserById getUserById,
            UpdateUserByAdmin updateUserByAdmin,
            DeleteUserByAdmin deleteUserByAdmin,
            UserRestDtoMapper mapper) {
        this.createUserByAdmin = createUserByAdmin;
        this.listUser = listUser;
        this.getUserById = getUserById;
        this.updateUserByAdmin = updateUserByAdmin;
        this.deleteUserByAdmin = deleteUserByAdmin;
        this.mapper = mapper;
    }

    // ============================================
    // CREATE
    // ============================================

    /**
     * POST /api/admin/users
     * Crear nuevo usuario (solo ADMIN)
     */
    @PostMapping
    public ResponseEntity<CreateUserByAdminResponseDTO> createUser(
            @Valid @RequestBody CreateUserByAdminRequestDTO requestDTO) {

        logger.info("Admin creando usuario con email: {}", requestDTO.email());

        // 1. DTO → Command
        CreateUserByAdminCommand command = mapper.toCreateUserByAdminCommand(requestDTO);

        // 2. Ejecutar UseCase
        User createdUser = createUserByAdmin.handle(command);

        // 3. Domain → DTO
        CreateUserByAdminResponseDTO responseDTO = mapper.toCreateUserByAdminResponseDTO(createdUser);

        logger.info("Usuario creado exitosamente con ID: {}", createdUser.getIdUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    // ============================================
    // READ
    // ============================================

    /**
     * GET /api/admin/users?page=0&size=10&sortBy=idUser
     * Listar todos los usuarios con paginación
     */
    @GetMapping
    public ResponseEntity<ListUserResponseDTO> listAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idUser") String sortBy) {

        logger.debug("Admin listando usuarios - page: {}, size: {}, sortBy: {}", page, size, sortBy);

        // 1. Crear Query
        ListUsersQuery query = new ListUsersQuery(page, size, sortBy);

        // 2. Ejecutar UseCase
        PageResponse<User> pageResponse = listUser.execute(query);

        // 3. PageResponse<User> → DTO
        ListUserResponseDTO responseDTO = mapper.toListUserResponseDTO(pageResponse);

        logger.debug("Retornando {} usuarios de {} totales",
                pageResponse.content().size(),
                pageResponse.totalElements());

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * GET /api/admin/users/{id}
     * Obtener usuario por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Integer id) {
        logger.debug("Admin buscando usuario por ID: {}", id);

        // 1. Ejecutar UseCase
        User user = getUserById.handle(id);

        // 2. Domain → DTO
        UserResponseDTO responseDTO = mapper.toUserResponseDTO(user);

        logger.debug("Usuario encontrado: {}", user.getEmail());
        return ResponseEntity.ok(responseDTO);
    }

    // ============================================
    // UPDATE
    // ============================================

    /**
     * PUT /api/admin/users/{id}
     * Actualizar usuario
     */
    @PutMapping("/{id}")
    public ResponseEntity<UpdateUserByAdminResponseDTO> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUserByAdminRequestDTO updateDTO) {

        logger.info("Admin actualizando usuario con ID: {}", id);

        // 1. DTO → Command
        UpdateUserByAdminCommand command = mapper.toUpdateUserByAdminCommand(id, updateDTO);

        // 2. Ejecutar UseCase
        User updatedUser = updateUserByAdmin.handle(command);

        // 3. Domain → DTO
        UpdateUserByAdminResponseDTO responseDTO = mapper.toUpdateUserByAdminResponseDTO(updatedUser);

        logger.info("Usuario actualizado exitosamente con ID: {}", id);
        return ResponseEntity.ok(responseDTO);
    }

    // ============================================
    // DELETE
    // ============================================

    /**
     * DELETE /api/admin/users/{id}
     * Eliminar usuario (soft delete)
     * ✅ SOLO 1 método - sin duplicado
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteUserByAdminDTO> deleteUserByAdmin(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        logger.info("Admin eliminando usuario con ID: {}", id);

        Integer adminId = getUserIdFromDetails(userDetails);
        DeleteUserByAdminCommand command = new DeleteUserByAdminCommand(id, adminId);

        // Ejecutar UseCase (Soft Delete)
        deleteUserByAdmin.handle(command);

        logger.info("Usuario {} marcado como eliminado por admin {}", id, adminId);

        // ✅ Retorna DTO con mensaje
        DeleteUserByAdminDTO response = mapper.toDeleteUserByAdminDTO(id);
        return ResponseEntity.ok(response);  // ✅ OK en lugar de noContent()
    }




    // ============================================
    // MÉTODOS PRIVADOS (HELPERS)
    // ============================================

    /**
     * Extrae el ID del usuario desde CustomUserDetails
     * ✅ Auditoría: registra quién hizo la acción
     */
    private Integer getUserIdFromDetails(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails) {
            return ((CustomUserDetails) userDetails).getId();
        }
        throw new RuntimeException("UserDetails no es CustomUserDetails");
    }
}
