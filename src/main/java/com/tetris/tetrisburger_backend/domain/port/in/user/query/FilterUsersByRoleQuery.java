package com.tetris.tetrisburger_backend.domain.port.in.user.query;

import com.tetris.tetrisburger_backend.domain.model.Role;

public record FilterUsersByRoleQuery(
        Role role,
        int page,
        int size,
        String sortBy
) {
    public FilterUsersByRoleQuery {
        if (role == null) {
            throw new IllegalArgumentException("Role no puede ser nulo");
        }
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        if (sortBy == null || sortBy.isBlank()) sortBy = "idUser";
    }
}
