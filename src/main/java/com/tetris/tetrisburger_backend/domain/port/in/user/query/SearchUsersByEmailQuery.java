package com.tetris.tetrisburger_backend.domain.port.in.user.query;

public record SearchUsersByEmailQuery(
        String email
) {
    public SearchUsersByEmailQuery {
        if (email.isBlank() ||email == null  ){
            throw new IllegalArgumentException("Debes escribir algun email");
        }
        email = email.trim();
    }
}
