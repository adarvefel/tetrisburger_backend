package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.DeleteProfileUser;
import com.tetris.tetrisburger_backend.domain.port.in.user.GetUserByEmail;
import com.tetris.tetrisburger_backend.domain.port.in.user.GetUserProfile;
import com.tetris.tetrisburger_backend.domain.port.in.user.UpdateProfileUser;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.DeleteProfileUserCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateProfileUserCommand;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.GetUserProfileQuery;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.user.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.UserRestDtoMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para perfil del usuario autenticado
 * El usuario solo puede ver y modificar su propio perfil
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    private final GetUserProfile getUserProfile;
    private final UpdateProfileUser updateProfileUser;
    private final DeleteProfileUser deleteProfileUser;
    private final UserRestDtoMapper mapper;

    public ProfileController(
            GetUserProfile getUserProfile,
            UpdateProfileUser updateProfileUser,
            DeleteProfileUser deleteProfileUser,
            UserRestDtoMapper mapper) {
        this.getUserProfile = getUserProfile;
        this.updateProfileUser = updateProfileUser;
        this.deleteProfileUser = deleteProfileUser;
        this.mapper = mapper;
    }

    /**
     * GET /api/profile - Obtener perfil del usuario autenticado
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GetUserProfileResponseDTO> getMyProfile() {
        logger.debug("Obteniendo perfil del usuario autenticado");

        User user = getUserProfile.execute(new GetUserProfileQuery());
        GetUserProfileResponseDTO responseDTO = mapper.toGetUserProfileResponseDTO(user);

        logger.info("Perfil obtenido para: {}", user.getEmail());
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * PATCH /api/profile - Actualizar perfil del usuario autenticado
     */
    @PatchMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UpdateProfileUserResponseDTO> updateMyProfile(
            @Valid @RequestBody UpdateProfileUserRequestDTO requestDTO) {

        logger.debug("Actualizando perfil del usuario autenticado");

        // 1. Obtener usuario autenticado
        User currentUser = getUserProfile.execute(new GetUserProfileQuery());

        // 2. DTO → Command
        UpdateProfileUserCommand command = mapper.toUpdateProfileUserCommand(
                currentUser.getIdUser(),
                requestDTO
        );

        // 3. Ejecutar (interface)
        User updatedUser = updateProfileUser.handle(currentUser.getIdUser(), command);

        // 4. Domain → DTO
        UpdateProfileUserResponseDTO responseDTO = mapper.toUpdateProfileUserResponseDTO(updatedUser);

        logger.info("Perfil actualizado para: {}", currentUser.getEmail());
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * DELETE /api/profile - Eliminar cuenta del usuario autenticado
     */
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DeleteProfileUserDTO> deleteMyAccount() {

        logger.debug("Eliminando cuenta del usuario autenticado");

        // 1. Obtener usuario autenticado
        User currentUser = getUserProfile.execute(new GetUserProfileQuery());

        // 2. DTO → Command
        DeleteProfileUserCommand command = mapper.toDeleteProfileUserCommand(currentUser.getIdUser());

        // 3. Ejecutar (interface)
        deleteProfileUser.handle(command);

        // 4. Respuesta
        DeleteProfileUserDTO responseDTO = mapper.toDeleteProfileUserDTO(currentUser.getIdUser());

        logger.info("Cuenta eliminado para: {}", currentUser.getEmail());
        return ResponseEntity.ok(responseDTO);
    }

}


