package com.tetris.tetrisburger_backend.domain.common;

import com.tetris.tetrisburger_backend.domain.model.User;

/**
 * Respuesta de autenticación del dominio
 * Record = Inmutable, sin setters
 */
public record LoginResponse(
        String token,
        User user,
        long expiresIn
) {
    public LoginResponse {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("El token no puede estar vacío");
        }
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (expiresIn <= 0) {
            throw new IllegalArgumentException("El tiempo de expiración debe ser positivo");
        }
    }
}
