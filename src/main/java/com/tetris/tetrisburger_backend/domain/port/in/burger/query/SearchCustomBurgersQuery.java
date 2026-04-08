package com.tetris.tetrisburger_backend.domain.port.in.burger.query;

public record SearchCustomBurgersQuery(
        Integer idUser,
        String name
) {
    public SearchCustomBurgersQuery {
        if (idUser == null || idUser <= 0) {
            throw new IllegalArgumentException("El ID del usuario es requerido");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre de búsqueda es requerido");
        }
    }
}