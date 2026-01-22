package com.tetris.tetrisburger_backend.infrastructure.rest.dto.user;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO para actualización parcial (PATCH) del perfil
 */
public record UpdateProfileUserRequestDTO(

        @Schema(description = "Nombre", example = "Juan Perez", nullable = true)
        @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        String userName,

        @Schema(description = "Contraseña", example = "NuevaPassword123", nullable = true, minLength = 6)
        @Size(min = 6, message = "La contraseña debe tener mínimo 6 caracteres")
        String password,



        @Schema(description = "Teléfono", example = "3001234567", nullable = true)
        String phone
) {}
