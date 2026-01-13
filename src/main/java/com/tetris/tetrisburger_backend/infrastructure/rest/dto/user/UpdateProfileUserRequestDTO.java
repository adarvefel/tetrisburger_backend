package com.tetris.tetrisburger_backend.infrastructure.rest.dto.user;


import org.springframework.web.multipart.MultipartFile;

/**
 * DTO para actualización parcial (PATCH) del perfil
 */
public record UpdateProfileUserRequestDTO(
        String userName,
        String password,
        MultipartFile userImage,
        String phone
) {}
