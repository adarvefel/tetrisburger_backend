package com.tetris.tetrisburger_backend.infrastructure.rest.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(
        name = "ForgotPasswordRequestDTO",
        description = "Solicitud para enviar correo de recuperación de contraseña."
)
public record ForgotPasswordRequestDTO(

        @Schema(
                description = "Correo del usuario que solicita recuperación.",
                example = "cama8@gmail.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "El email es requerido")
        @Email(message = "Escribe tu correo para recuperar tu contraseña")
        String email,
        String recaptchaToken

) {}
