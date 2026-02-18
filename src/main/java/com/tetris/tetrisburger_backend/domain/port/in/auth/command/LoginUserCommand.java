package com.tetris.tetrisburger_backend.domain.port.in.auth.command;

public record LoginUserCommand(
        String email,
        String password,
        String recaptchaToken
) {
    public  LoginUserCommand{
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es requerido");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña es requerida");
        }
        if (recaptchaToken == null || recaptchaToken.isBlank()) {
            throw new IllegalArgumentException("El token de reCAPTCHA es requerido");
        }

    }
}
