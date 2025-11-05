package com.tetris.tetrisburger_backend.infrastructure.rest.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserByAdminRequestDTO(
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 2, max = 100, message = "El nombre debe tener al menos 2 caracteres")
        String userName,

        @NotBlank(message = "El email es requerido")
        @Email(message = "Debe ser un email válido")
        String email,

        @NotBlank(message = "La contraseña es requerida")
        @Size(min = 6, message = "La contraseña debe tener por lo menos 6 caracteres")
        String password,
        String userImage,
        String role,
        String phone
        
) {
}
