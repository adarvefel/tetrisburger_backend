package com.tetris.tetrisburger_backend.infrastructure.rest.dto.user;


/**
 * DTO para actualización parcial (PATCH) del perfil
 */
public record UpdateProfileUserRequestDTO(
        String userName,
        String password,
        String userImage,
        String phone
) {}
