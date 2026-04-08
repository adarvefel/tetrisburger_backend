package com.tetris.tetrisburger_backend.domain.port.in.auth.command;

public record ResetPasswordCommand(
        String token,
        String newPassword
) {
    public ResetPasswordCommand{
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("El token es requirido");

        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("La nueva contraseña es requerida");

        }
        if (newPassword.length() < 8 ){
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres ");

        }

    }
}
