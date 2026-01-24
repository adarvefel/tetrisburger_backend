package com.tetris.tetrisburger_backend.infrastructure.rest.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        name = "ResetPasswordRequestDTO",
        description = "Solicitud para restablecer la contraseña usando el token enviado al correo."
)
public record ResetPasswordRequestDTO(

        @Schema(
                description = "Token de recuperación (password_reset).",
                example = "eyJhbGciOiJIUzI1NiJ9...",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El token es requerido")
        String token,

        @Schema(
                description = "Nueva contraseña (mínimo 6 caracteres).",
                example = "tocame2026",
                minLength = 6,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "La nueva contraseña es requerida")
        @Size(min = 6, message = "La nueva contraseña debe tener al menos 6 caracteres")
        String newPassword

) {}
