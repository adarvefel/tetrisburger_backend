package com.tetris.tetrisburger_backend.infrastructure.rest.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "Datos para crear un usuario (solo ADMIN). Si se envía userImage, el endpoint debe ser multipart/form-data.")
public record CreateUserByAdminRequestDTO(

        @Schema(description = "Nombre del usuario", example = "joseComeGordas")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 2, max = 100, message = "El nombre debe tener al menos 2 caracteres")
        String userName,

        @Schema(description = "Email del usuario", example = "jose56@gmail.com")
        @NotBlank(message = "El email es requerido")
        @Email(message = "Debe ser un email válido")
        String email,

        @Schema(description = "Contraseña", example = "amogorda1234", minLength = 6)
        @NotBlank(message = "La contraseña es requerida")
        @Size(min = 6, message = "La contraseña debe tener por lo menos 6 caracteres")
        String password,

        @Schema(description = "Imagen del usuario (archivo)", type = "string", format = "binary", nullable = true)
        MultipartFile userImage,

        @Schema(description = "Rol del usuario", example = "EMPLOYEE", allowableValues = {"ADMIN", "EMPLOYEE", "CLIENT"})
        String role,

        @Schema(description = "Teléfono", example = "3017342342", nullable = true)
        String phone

) {}
