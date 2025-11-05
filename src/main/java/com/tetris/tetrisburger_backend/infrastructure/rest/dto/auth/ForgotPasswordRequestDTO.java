package com.tetris.tetrisburger_backend.infrastructure.rest.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequestDTO(
        @NotBlank(message = "El email es requerido")
        @Email(message = "Escribe tu correo para recuperar tu contraseña")
        String email
){

}
