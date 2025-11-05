package com.tetris.tetrisburger_backend.infrastructure.rest.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDTO(
    @NotBlank
    String token,

    @NotBlank
    @Size(min= 6,message = "La nueva contraseña debe tener al menos 6 caracterea")
    String newPassword
    ){


}
