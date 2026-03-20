package com.tetris.tetrisburger_backend.domain.port.in.user.command;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.model.Role;

public record UpdateUserByAdminCommand(
        Integer idUser,
        String userName,
        String email,
        String password,
        Role role,
        String phone,
        Integer updatedBy
) {}
