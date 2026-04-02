package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.ImageStatus;
import com.tetris.tetrisburger_backend.domain.exception.UnauthorizedException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.DeleteProfileUser;
import com.tetris.tetrisburger_backend.domain.port.in.user.GetUserProfile;
import com.tetris.tetrisburger_backend.domain.port.in.user.UpdateProfileImage;
import com.tetris.tetrisburger_backend.domain.port.in.user.UpdateProfileUser;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.DeleteProfileUserCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateProfileImageCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateProfileUserCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.GetUserProfileQuery;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.user.DeleteProfileUserDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.user.GetUserProfileResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.user.UpdateProfileUserRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.user.UpdateProfileUserResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.UserRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.validator.ImageValidator;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profile", description = "Gestión del perfil del usuario autenticado")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    private final GetUserProfile getUserProfile;
    private final UpdateProfileUser updateProfileUser;
    private final UpdateProfileImage updateProfileImage;
    private final DeleteProfileUser deleteProfileUser;
    private final ImageStoragePort imageStoragePort;
    private final UserRestDtoMapper mapper;
    private final ImageValidator imageValidator;

    public ProfileController(
            GetUserProfile getUserProfile,
            UpdateProfileUser updateProfileUser,
            UpdateProfileImage updateProfileImage,
            DeleteProfileUser deleteProfileUser,
            ImageStoragePort imageStoragePort,
            UserRestDtoMapper mapper,
            ImageValidator imageValidator
    ) {
        this.getUserProfile = getUserProfile;
        this.updateProfileUser = updateProfileUser;
        this.updateProfileImage = updateProfileImage;
        this.deleteProfileUser = deleteProfileUser;
        this.imageStoragePort = imageStoragePort;
        this.mapper = mapper;
        this.imageValidator = imageValidator;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Obtener mi perfil",
            description = "Retorna datos del usuario autenticado. imageStatus puede ser: NONE, PENDING o UPLOADED."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil retornado",
                    content = @Content(schema = @Schema(implementation = GetUserProfileResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<GetUserProfileResponseDTO> getMyProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer currentUserId = getUserIdFromDetails(userDetails);
        User user = getUserProfile.execute(new GetUserProfileQuery(currentUserId));

        String imageUrl = resolveImageUrl(user);
        ImageStatus imageStatus = resolveImageStatus(false, user.getUserImageKey());

        return ResponseEntity.ok(mapper.toGetUserProfileResponseDTO(user, imageUrl, imageStatus));
    }

    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Actualizar perfil (solo datos)",
            description = "Actualiza userName, password y/o phone. Para actualizar imagen usar PUT /api/profile/image."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil actualizado",
                    content = @Content(schema = @Schema(implementation = UpdateProfileUserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<UpdateProfileUserResponseDTO> updateMyProfileJson(
            @Valid @RequestBody UpdateProfileUserRequestDTO requestDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer currentUserId = getUserIdFromDetails(userDetails);

        UpdateProfileUserCommand command = mapper.toUpdateProfileUserCommand(currentUserId, requestDTO);
        User updatedUser = updateProfileUser.handle(currentUserId, command);

        String imageUrl = resolveImageUrl(updatedUser);
        ImageStatus imageStatus = resolveImageStatus(false, updatedUser.getUserImageKey());

        return ResponseEntity.ok(mapper.toUpdateProfileUserResponseDTO(updatedUser, imageUrl, imageStatus));
    }



    @PutMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Actualizar solo imagen",
            description = "Actualiza únicamente la imagen de perfil. Responde con imageStatus PENDING (consulta GET /api/profile para verificar cuando esté UPLOADED)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imagen aceptada (en proceso)",
                    content = @Content(schema = @Schema(implementation = UpdateProfileUserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Archivo inválido"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<UpdateProfileUserResponseDTO> updateMyProfileImage(
            @Parameter(description = "Imagen de perfil (JPG, PNG, WEBP)")
            @RequestPart("userImage") MultipartFile userImage,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer currentUserId = getUserIdFromDetails(userDetails);

        // Validar imagen
        imageValidator.validate(userImage);

        UpdateProfileImageCommand command = mapper.toUpdateProfileImageCommand(
                currentUserId,
                userImage
        );

        User user = updateProfileImage.handle(currentUserId, command);

        String imageUrl = resolveImageUrl(user);
        ImageStatus imageStatus = ImageStatus.PENDING;

        return ResponseEntity.ok(mapper.toUpdateProfileUserResponseDTO(user, imageUrl, imageStatus));
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Eliminar mi cuenta",
            description = "Realiza soft delete del usuario autenticado (marca deletedAt pero no borra físicamente)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta eliminada",
                    content = @Content(schema = @Schema(implementation = DeleteProfileUserDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<DeleteProfileUserDTO> deleteMyAccount(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer currentUserId = getUserIdFromDetails(userDetails);

        deleteProfileUser.handle(new DeleteProfileUserCommand(currentUserId));

        return ResponseEntity.ok(mapper.toDeleteProfileUserDTO(currentUserId));
    }

    // ============================================
    // MÉTODOS HELPER PRIVADOS
    // ============================================

    /**
     * Extrae el ID del usuario autenticado desde UserDetails
     */
    private Integer getUserIdFromDetails(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getId();
        }
        throw new UnauthorizedException("Usuario no autenticado correctamente");
    }

    /**
     * Convierte el imageKey almacenado en BD a URL completa de S3
     */
    private String resolveImageUrl(User user) {
        if (user == null || user.getUserImageKey() == null || user.getUserImageKey().isBlank()) {
            return null;
        }
        return imageStoragePort.getImageUrl(user.getUserImageKey());
    }

    /**
     * Determina el ImageStatus según el flujo:
     * - NONE: No hay imagen
     * - PENDING: Imagen en procesamiento asíncrono
     * - UPLOADED: Imagen disponible
     */
    private ImageStatus resolveImageStatus(boolean imageWasSent, String imageKey) {
        if (!imageWasSent) {
            // GET/PATCH sin imagen: Consulta de usuario existente
            return (imageKey == null || imageKey.isBlank()) ? ImageStatus.NONE : ImageStatus.UPLOADED;
        }

        // PATCH/PUT con imagen: Actualización con nueva imagen
        if (imageKey == null || imageKey.isBlank()) {
            return ImageStatus.PENDING;
        }

        return ImageStatus.UPLOADED;
    }
}
