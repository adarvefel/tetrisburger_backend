package com.tetris.tetrisburger_backend.domain.port.in.auth.command;

public record ForgotPasswordCommand(
        String email
) {
    public ForgotPasswordCommand {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es requerido");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
    }
}
