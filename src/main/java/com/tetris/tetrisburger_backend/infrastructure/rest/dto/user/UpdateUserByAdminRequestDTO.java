package com.tetris.tetrisburger_backend.infrastructure.rest.dto.user;

import jakarta.validation.constraints.*;

public record UpdateUserByAdminRequestDTO(
        @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        String userName,

        @Email(message = "Ingresa un email válido")
        String email,

        @Size(min = 6, message = "La contraseña debe tener mínimo 6 caracteres")
        String password,
        String userImage,

        String role  ,
        String phone

        ) {}
