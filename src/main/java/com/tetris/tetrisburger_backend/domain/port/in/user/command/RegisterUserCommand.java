package com.tetris.tetrisburger_backend.domain.port.in.user.command;

public record RegisterUserCommand(
        String userName,
        String email,
        String password



) {
        public RegisterUserCommand {
            // Validar userName
            if (userName == null || userName.isBlank()) {
                throw new IllegalArgumentException("EL nombre del usuario es requerido");
            }
            if (userName.length() < 3 || userName.length() > 50) {
                throw new IllegalArgumentException("El nombre del usuario debe de tener un nombre entre 2 y 50 caracteres ");
            }

            // Validar email
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("El email es requerido");
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("El fomarto del email es erroneo");
            }

            // Validar password
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("La contraseña es requerida");
            }
            if (password.length() < 6) {
                throw new IllegalArgumentException("La contraseña debe de tener al menos 6 caracteres");
            }


        }

}
