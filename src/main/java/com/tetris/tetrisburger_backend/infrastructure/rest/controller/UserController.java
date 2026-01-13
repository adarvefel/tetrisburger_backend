package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.*;
        import com.tetris.tetrisburger_backend.domain.port.in.user.command.CreateUserByAdminCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.DeleteUserByAdminCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateUserByAdminCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.ListUsersQuery;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.user.*;
        import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.UserRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Usuarios", description = "Gestión de usuarios (solo ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final CreateUserByAdmin createUserByAdmin;
    private final ListUser listUser;
    private final GetUserById getUserById;
    private final UpdateUserByAdmin updateUserByAdmin;
    private final DeleteUserByAdmin deleteUserByAdmin;
    private final UserRestDtoMapper mapper;
    private final ImageStoragePort imageStoragePort;
    private final SearchUsersByEmail  searchUsersByEmail;

    public UserController(CreateUserByAdmin createUserByAdmin, ListUser listUser, GetUserById getUserById, UpdateUserByAdmin updateUserByAdmin, DeleteUserByAdmin deleteUserByAdmin, UserRestDtoMapper mapper, ImageStoragePort imageStoragePort, SearchUsersByEmail searchUsersByEmail) {
        this.createUserByAdmin = createUserByAdmin;
        this.listUser = listUser;
        this.getUserById = getUserById;
        this.updateUserByAdmin = updateUserByAdmin;
        this.deleteUserByAdmin = deleteUserByAdmin;
        this.mapper = mapper;
        this.imageStoragePort = imageStoragePort;
        this.searchUsersByEmail = searchUsersByEmail;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Crear usuario",
            description = "Crea un nuevo usuario con rol específico (solo ADMIN). " +
                    "Consume multipart/form-data para permitir subir `userImage` como archivo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado (no es ADMIN)")
    })
    public ResponseEntity<CreateUserByAdminResponseDTO> createUser(
            @Valid @ModelAttribute CreateUserByAdminRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        logger.info("Admin creando usuario con email: {}", requestDTO.email());

        Integer adminId = getUserIdFromDetails(userDetails);

        CreateUserByAdminCommand command = mapper.toCreateUserByAdminCommand(requestDTO, adminId);

        User createdUser = createUserByAdmin.handle(command);

        if (createdUser.getUserImageKey() != null) {
            imageStoragePort.getImageUrl(createdUser.getUserImageKey());
        }

        CreateUserByAdminResponseDTO responseDTO = new CreateUserByAdminResponseDTO(
                createdUser.getIdUser(),
                createdUser.getUserName(),
                createdUser.getEmail(),
                createdUser.getUserImage(),
                createdUser.getRole().name(),
                createdUser.getPhone(),
                createdUser.getCreatedAt(),
                createdUser.getUpdatedAt(),
                createdUser.getCreatedBy(),
                createdUser.getUpdatedBy()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Retorna lista paginada de usuarios (solo ADMIN).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado retornado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado (no es ADMIN)")
    })
    public ResponseEntity<ListUserResponseDTO> listAllUsers(
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Tamaño de página", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Campo para ordenar", example = "idUser")
            @RequestParam(defaultValue = "idUser") String sortBy) {

        ListUsersQuery query = new ListUsersQuery(page, size, sortBy);

        PageResponse<User> pageResponse = listUser.execute(query);

        ListUserResponseDTO responseDTO = mapper.toListUserResponseDTO(pageResponse);

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Retorna los datos de un usuario específico (solo ADMIN).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario retornado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado (no es ADMIN)")
    })
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "ID del usuario", example = "3")
            @PathVariable Integer id) {

        User user = getUserById.handle(id);

        if (user.getUserImageKey() != null) {
            imageStoragePort.getImageUrl(user.getUserImageKey());
        }

        UserResponseDTO responseDTO = new UserResponseDTO(
                user.getIdUser(),
                user.getUserName(),
                user.getEmail(),
                user.getUserImage(),
                user.getPhone(),
                user.getRole().name(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getDeletedAt(),
                user.getCreatedBy(),
                user.getUpdatedBy(),
                user.getDeletedBy()
        );

        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza datos del usuario (solo ADMIN). " +
                    "Consume multipart/form-data para permitir actualizar `userImage` como archivo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado (no es ADMIN)")
    })
    public ResponseEntity<UpdateUserByAdminResponseDTO> updateUser(
            @Parameter(description = "ID del usuario", example = "3")
            @PathVariable Integer id,
            @Valid @ModelAttribute UpdateUserByAdminRequestDTO updateDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        Integer adminId = getUserIdFromDetails(userDetails);

        UpdateUserByAdminCommand command = mapper.toUpdateUserByAdminCommand(id, updateDTO, adminId);

        User updatedUser = updateUserByAdmin.handle(command);

        if (updatedUser.getUserImageKey() != null) {
            imageStoragePort.getImageUrl(updatedUser.getUserImageKey());
        }

        UpdateUserByAdminResponseDTO responseDTO = new UpdateUserByAdminResponseDTO(
                updatedUser.getIdUser(),
                updatedUser.getUserName(),
                updatedUser.getEmail(),
                updatedUser.getUserImage(),
                updatedUser.getRole().name(),
                updatedUser.getPhone(),
                updatedUser.getCreatedAt(),
                updatedUser.getUpdatedAt(),
                updatedUser.getCreatedBy(),
                updatedUser.getUpdatedBy()
        );

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "Realiza soft delete del usuario (solo ADMIN).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado (no es ADMIN)")
    })
    public ResponseEntity<DeleteUserByAdminDTO> deleteUserByAdmin(
            @Parameter(description = "ID del usuario", example = "4")
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Integer adminId = getUserIdFromDetails(userDetails);

        DeleteUserByAdminCommand command = new DeleteUserByAdminCommand(id, adminId);

        deleteUserByAdmin.handle(command);

        DeleteUserByAdminDTO response = mapper.toDeleteUserByAdminDTO(id);
        return ResponseEntity.ok(response);
    }

    private Integer getUserIdFromDetails(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getId();
        }
        throw new IllegalStateException("UserDetails no es CustomUserDetails");
    }

    @GetMapping("/by-email")
    @Operation(
            summary = "Buscar usuarios por email",
            description = "Retorna usuarios cuyo email contiene el texto indicado (solo ADMIN). Ej: email=jose"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuarios retornados"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado (no es ADMIN)")
    })
    public ResponseEntity<List<UserResponseDTO>> getUsersByEmail(
            @Parameter(description = "Texto a buscar dentro del email", example = "jose")
            @RequestParam String email
    ) {
        List<User> users = searchUsersByEmail.handle(email);
        return ResponseEntity.ok(mapper.toUserResponseDTOList(users));
    }
}
