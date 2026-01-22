package com.tetris.tetrisburger_backend.infrastructure.rest.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(description = "Actualización de usuario por ADMIN vía JSON (sin imagen). Campos opcionales.")
public record UpdateUserByAdminRequestDTO(
        @Schema(description = "Nombre", example = "Saralegusta238", nullable = true)
        @Size(min = 3, max = 50)
        String userName,

        @Schema(description = "Email", example = "sara@gmail.com", nullable = true)
        @Email
        String email,

        @Schema(description = "Contraseña", example = "nuevaPass1234", nullable = true, minLength = 6)
        @Size(min = 6)
        String password,

        @Schema(description = "Rol", example = "EMPLOYEE", allowableValues = {"ADMIN","EMPLOYEE","CLIENT"}, nullable = true)
        String role,

        @Schema(description = "Teléfono", example = "3001234567", nullable = true)
        String phone
) {}
