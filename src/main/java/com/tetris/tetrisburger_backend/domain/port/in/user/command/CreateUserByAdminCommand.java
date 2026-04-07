package com.tetris.tetrisburger_backend.domain.port.in.user.command;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.enums.Role;

public record CreateUserByAdminCommand(

        String userName,
        String email,
        String password,
        FileData userImage,
        Role role,
        String phone,
        Integer createdBy){



}
