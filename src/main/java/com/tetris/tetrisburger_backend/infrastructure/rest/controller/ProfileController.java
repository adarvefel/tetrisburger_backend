package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.DeleteProfileUser;
import com.tetris.tetrisburger_backend.domain.port.in.user.GetUserProfile;
import com.tetris.tetrisburger_backend.domain.port.in.user.UpdateProfileUser;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.DeleteProfileUserCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateProfileUserCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.GetUserProfileQuery;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.user.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.UserRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Perfil", description = "Operaciones del usuario autenticado sobre su propio perfil")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    private final GetUserProfile getUserProfile;
    private final UpdateProfileUser updateProfileUser;
    private final DeleteProfileUser deleteProfileUser;
    private final ImageStoragePort imageStoragePort;
    private final UserRestDtoMapper mapper;

    public ProfileController(
            GetUserProfile getUserProfile,
            UpdateProfileUser updateProfileUser,
            DeleteProfileUser deleteProfileUser,
            ImageStoragePort imageStoragePort,
            UserRestDtoMapper mapper) {
        this.getUserProfile = getUserProfile;
        this.updateProfileUser = updateProfileUser;
        this.deleteProfileUser = deleteProfileUser;
        this.imageStoragePort = imageStoragePort;
        this.mapper = mapper;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Obtener mi perfil",
            description = "Retorna los datos del usuario autenticado. Requiere Authorization: Bearer <token>."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil retornado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<GetUserProfileResponseDTO> getMyProfile() {
        User user = getUserProfile.execute(new GetUserProfileQuery());

        if (user.getUserImageKey() != null) {
            imageStoragePort.getImageUrl(user.getUserImageKey());
        }

        GetUserProfileResponseDTO responseDTO = new GetUserProfileResponseDTO(
                user.getIdUser(),
                user.getUserName(),
                user.getEmail(),
                user.getUserImage(),
                user.getRole().name(),
                user.getPhone(),
                user.getCreatedAt()
        );

        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Actualizar mi perfil",
            description = "Actualiza parcialmente el perfil. Consume multipart/form-data para permitir `userImage` como archivo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<UpdateProfileUserResponseDTO> updateMyProfile(
            @Valid @ModelAttribute UpdateProfileUserRequestDTO requestDTO,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        Integer currentUserId = getUserIdFromDetails(userDetails);

        UpdateProfileUserCommand command = mapper.toUpdateProfileUserCommand(currentUserId, requestDTO);

        User updatedUser = updateProfileUser.handle(currentUserId, command);

        if (updatedUser.getUserImageKey() != null) {
            imageStoragePort.getImageUrl(updatedUser.getUserImageKey());
        }

        UpdateProfileUserResponseDTO responseDTO = new UpdateProfileUserResponseDTO(
                updatedUser.getIdUser(),
                updatedUser.getUserName(),
                updatedUser.getEmail(),
                updatedUser.getUserImage(),
                updatedUser.getPhone(),
                updatedUser.getCreatedAt(),
                updatedUser.getUpdatedAt(),
                updatedUser.getCreatedBy(),
                updatedUser.getUpdatedBy()
        );

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Eliminar mi cuenta",
            description = "Realiza soft delete del usuario autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta eliminada (soft delete)"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<DeleteProfileUserDTO> deleteMyAccount(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        Integer currentUserId = getUserIdFromDetails(userDetails);

        DeleteProfileUserCommand command = mapper.toDeleteProfileUserCommand(currentUserId);

        deleteProfileUser.handle(command);

        DeleteProfileUserDTO responseDTO = mapper.toDeleteProfileUserDTO(currentUserId);

        return ResponseEntity.ok(responseDTO);
    }

    private Integer getUserIdFromDetails(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getId();
        }
        throw new IllegalStateException("UserDetails no es CustomUserDetails");
    }
}
