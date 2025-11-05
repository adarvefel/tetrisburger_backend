package com.tetris.tetrisburger_backend.domain.port.in.user.command;

import com.tetris.tetrisburger_backend.domain.model.Role;

public record UpdateUserByAdminCommand(
        Integer idUser,
        String userName,
        String email,
        String password,
        String userImage,
        Role role,
        String phone

) {
}
