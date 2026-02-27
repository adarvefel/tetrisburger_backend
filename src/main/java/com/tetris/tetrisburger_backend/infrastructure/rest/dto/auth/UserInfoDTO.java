package com.tetris.tetrisburger_backend.infrastructure.rest.dto.auth;

/**
 * Información básica del usuario para respuesta de login
 * No expone información sensible como password
 */
public record UserInfoDTO(
        Integer idUser,
        String userName,
        String email,
        String role
) {}
