package com.tetris.tetrisburger_backend.domain.port.in.burger.query;

public record SearchMenuBurgersQuery(
        String name
) {
    public SearchMenuBurgersQuery {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre de búsqueda es requerido");
        }
    }
}