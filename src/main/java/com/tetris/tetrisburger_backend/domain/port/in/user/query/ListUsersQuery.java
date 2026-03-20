package com.tetris.tetrisburger_backend.domain.port.in.user.query;

public record ListUsersQuery(
        int page,
        int size,
        String sortBy
) {
    // Constructor compacto - valida ANTES de asignar
    public ListUsersQuery {
        if (page < 0) {
            throw new IllegalArgumentException("La página no puede ser negativa");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("El tamaño debe estar entre 1 y 100");
        }
        if (sortBy == null || sortBy.isBlank()) {
            throw new IllegalArgumentException("El campo de ordenamiento es requerido");
        }
    }
}
