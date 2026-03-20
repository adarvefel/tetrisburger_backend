package com.tetris.tetrisburger_backend.domain.port.in.auth.command;

public record LoginWithGoogleCommand(
        String googleToken
) {
    public LoginWithGoogleCommand {
        if (googleToken == null || googleToken.isBlank()) {
            throw new IllegalArgumentException("El token de Google es requerido");
        }
    }
}
