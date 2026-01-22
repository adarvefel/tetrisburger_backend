package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.*;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.CreateUserByAdminCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.DeleteUserByAdminCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateUserByAdminCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateUserImageByAdminCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.ListUsersQuery;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.user.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.UserRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Users", description = "Gestión de usuarios (solo ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final CreateUserByAdmin createUserByAdmin;
    private final ListUser listUser;
    private final GetUserById getUserById;
    private final UpdateUserByAdmin updateUserByAdmin;
    private final UpdateUserImageByAdmin updateUserImageByAdmin;
    private final DeleteUserByAdmin deleteUserByAdmin;
    private final UserRestDtoMapper mapper;
    private final ImageStoragePort imageStoragePort;
    private final SearchUsersByEmail searchUsersByEmail;

    public UserController(CreateUserByAdmin createUserByAdmin,
                          ListUser listUser,
                          GetUserById getUserById,
                          UpdateUserByAdmin updateUserByAdmin,
                          UpdateUserImageByAdmin updateUserImageByAdmin,
                          DeleteUserByAdmin deleteUserByAdmin,
                          UserRestDtoMapper mapper,
                          ImageStoragePort imageStoragePort,
                          SearchUsersByEmail searchUsersByEmail) {
        this.createUserByAdmin = createUserByAdmin;
        this.listUser = listUser;
        this.getUserById = getUserById;
        this.updateUserByAdmin = updateUserByAdmin;
        this.updateUserImageByAdmin = updateUserImageByAdmin;
        this.deleteUserByAdmin = deleteUserByAdmin;
        this.mapper = mapper;
        this.imageStoragePort = imageStoragePort;
        this.searchUsersByEmail = searchUsersByEmail;
    }

    private Integer getUserIdFromDetails(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getId();
        }
        throw new IllegalStateException("UserDetails no es CustomUserDetails");
    }

    private String resolveImageUrlFromUser(User user) {
        if (user == null || user.getUserImageKey() == null || user.getUserImageKey().isBlank()) {
            return null;
        }
        return imageStoragePort.getImageUrl(user.getUserImageKey());
    }

    private String resolveImageStatus(boolean imageWasSent, String imageUrlResolvedFromUser) {
        if (imageWasSent) return "PENDING";
        if (imageUrlResolvedFromUser != null) return "READY";
        return "NONE";
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Crear usuario",
            description = "Crea un nuevo usuario. Si envías userImage, el imageStatus será PENDING (procesamiento asíncrono)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado",
                    content = @Content(schema = @Schema(implementation = CreateUserByAdminResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya existe"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No tienes rol ADMIN")
    })
    public ResponseEntity<CreateUserByAdminResponseDTO> createUser(
            @Parameter(description = "Datos del usuario (JSON como archivo)")
            @Valid @RequestPart("data") CreateUserByAdminRequestDTO requestDTO,
            @Parameter(description = "Imagen de perfil (opcional)")
            @RequestPart(value = "userImage", required = false) MultipartFile userImage,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer adminId = getUserIdFromDetails(userDetails);
        boolean imageWasSent = (userImage != null && !userImage.isEmpty());

        CreateUserByAdminCommand command = mapper.toCreateUserByAdminCommand(requestDTO, userImage, adminId);
        User createdUser = createUserByAdmin.handle(command);

        String imageUrl = resolveImageUrlFromUser(createdUser);
        String imageStatus = resolveImageStatus(imageWasSent, imageUrl);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toCreateUserByAdminResponseDTO(createdUser, imageUrl, imageStatus));
    }

    @GetMapping
    @Operation(summary = "Listar usuarios (paginado)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada",
                    content = @Content(schema = @Schema(implementation = ListUserResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No tienes rol ADMIN")
    })
    public ResponseEntity<ListUserResponseDTO> listAllUsers(
            @Parameter(description = "Número de página (base 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Elementos por página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenar") @RequestParam(defaultValue = "idUser") String sortBy
    ) {
        ListUsersQuery query = new ListUsersQuery(page, size, sortBy);
        PageResponse<User> pageResponse = listUser.execute(query);

        List<UserResponseDTO> contentWithUrls = pageResponse.content().stream()
                .map(u -> {
                    String imageUrl = resolveImageUrlFromUser(u);
                    String imageStatus = resolveImageStatus(false, imageUrl);
                    return mapper.toUserResponseDTO(u, imageUrl, imageStatus);
                })
                .toList();

        ListUserResponseDTO responseDTO = new ListUserResponseDTO(
                contentWithUrls,
                pageResponse.totalElements(),
                pageResponse.totalPages(),
                LocalDateTime.now()
        );

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No tienes rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Integer id) {
        User user = getUserById.handle(id);

        String imageUrl = resolveImageUrlFromUser(user);
        String imageStatus = resolveImageStatus(false, imageUrl);

        return ResponseEntity.ok(mapper.toUserResponseDTO(user, imageUrl, imageStatus));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Actualizar usuario (solo datos)",
            description = "Actualiza userName, password, role, phone. Para actualizar imagen usar PUT /api/admin/users/{id}/image"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado",
                    content = @Content(schema = @Schema(implementation = UpdateUserByAdminResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No tienes rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UpdateUserByAdminResponseDTO> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUserByAdminRequestDTO updateDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer adminId = getUserIdFromDetails(userDetails);

        UpdateUserByAdminCommand command = mapper.toUpdateUserByAdminCommand(id, updateDTO, adminId);
        User updatedUser = updateUserByAdmin.handle(command);

        String imageUrl = resolveImageUrlFromUser(updatedUser);
        String imageStatus = (imageUrl != null) ? "READY" : "NONE";

        return ResponseEntity.ok(mapper.toUpdateUserByAdminResponseDTO(updatedUser, imageUrl, imageStatus));
    }

    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Actualizar solo imagen",
            description = "Actualiza únicamente la imagen de perfil. Responde con imageStatus PENDING (consulta GET /api/admin/users/{id} para verificar cuando esté READY)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imagen aceptada (en proceso)",
                    content = @Content(schema = @Schema(implementation = UpdateUserByAdminResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Archivo inválido"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No tienes rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UpdateUserByAdminResponseDTO> updateUserImage(
            @PathVariable Integer id,
            @RequestPart("userImage") MultipartFile userImage,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer adminId = getUserIdFromDetails(userDetails);

        UpdateUserImageByAdminCommand command =
                mapper.toUpdateUserImageByAdminCommand(id, userImage, adminId);

        User user = updateUserImageByAdmin.handle(command);

        String imageUrl = resolveImageUrlFromUser(user);
        return ResponseEntity.ok(mapper.toUpdateUserByAdminResponseDTO(user, imageUrl, "PENDING"));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar usuario",
            description = "Realiza soft delete del usuario (marca deletedAt pero no borra físicamente)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario eliminado",
                    content = @Content(schema = @Schema(implementation = DeleteUserByAdminDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No tienes rol ADMIN"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<DeleteUserByAdminDTO> deleteUserByAdmin(
            @PathVariable Integer id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer adminId = getUserIdFromDetails(userDetails);

        DeleteUserByAdminCommand command = new DeleteUserByAdminCommand(id, adminId);
        deleteUserByAdmin.handle(command);

        return ResponseEntity.ok(mapper.toDeleteUserByAdminDTO(id));
    }

    @GetMapping("/by-email")
    @Operation(
            summary = "Buscar por email",
            description = "Busca usuarios cuyo email contenga el texto especificado (case-insensitive)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Búsqueda completada",
                    content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No tienes rol ADMIN")
    })
    public ResponseEntity<List<UserResponseDTO>> getUsersByEmail(
            @Parameter(description = "Texto a buscar en el email") @RequestParam String email
    ) {
        List<User> users = searchUsersByEmail.handle(email);

        List<UserResponseDTO> dtoList = users.stream()
                .map(u -> {
                    String imageUrl = resolveImageUrlFromUser(u);
                    String imageStatus = resolveImageStatus(false, imageUrl);
                    return mapper.toUserResponseDTO(u, imageUrl, imageStatus);
                })
                .toList();

        return ResponseEntity.ok(dtoList);
    }
}
