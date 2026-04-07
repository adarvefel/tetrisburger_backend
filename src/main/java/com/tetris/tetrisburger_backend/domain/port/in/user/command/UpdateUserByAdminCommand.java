package com.tetris.tetrisburger_backend.domain.port.in.user.command;

import com.tetris.tetrisburger_backend.domain.enums.Role;

public record UpdateUserByAdminCommand(
        Integer idUser,
        String userName,
        String email,
        String password,
        Role role,
        String phone,
        Integer updatedBy
) {}
