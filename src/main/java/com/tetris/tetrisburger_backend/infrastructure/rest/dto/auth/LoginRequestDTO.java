package com.tetris.tetrisburger_backend.infrastructure.rest.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
        name = "LoginRequestDTO",
        description = "Credenciales para autenticación. Retorna un token JWT si es válido."
)
public record LoginRequestDTO(

        @Schema(
                description = "Correo electrónico del usuario.",
                example = "adarvefelipe58@gmail.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El email es requerido")
        String email,

        @Schema(
                description = "Contraseña del usuario.",
                example = "felipe12345",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 1
        )
        @NotBlank(message = "La contraseña es requerida")
        String password,

        String recaptchaToken

) {}
