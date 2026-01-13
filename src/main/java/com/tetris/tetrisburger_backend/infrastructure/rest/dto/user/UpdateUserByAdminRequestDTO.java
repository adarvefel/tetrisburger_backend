package com.tetris.tetrisburger_backend.infrastructure.rest.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "Datos para actualizar un usuario (solo ADMIN). Campos opcionales; userImage es archivo.")
public record UpdateUserByAdminRequestDTO(

        @Schema(description = "Nombre", example = "Saralegustachimbo238", nullable = true)
        @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        String userName,

        @Schema(description = "Email", example = "sara@gmail.com", nullable = true)
        @Email(message = "Ingresa un email válido")
        String email,

        @Schema(description = "Contraseña", example = "nuevaPass1234", nullable = true, minLength = 6)
        @Size(min = 6, message = "La contraseña debe tener mínimo 6 caracteres")
        String password,

        @Schema(description = "Nueva imagen (archivo)", type = "string", format = "binary", nullable = true)
        MultipartFile userImage,

        @Schema(description = "Rol", example = "EMPLOYEE", allowableValues = {"ADMIN", "EMPLOYEE", "CLIENT"}, nullable = true)
        String role,

        @Schema(description = "Teléfono", example = "41421", nullable = true)
        String phone

) {}
