package com.tetris.tetrisburger_backend.domain.port.in.user.command;

import com.tetris.tetrisburger_backend.domain.common.FileData;

public record UpdateProfileUserCommand(
        Integer idUser,
        String userName,
        String password,
        String phone
){


}
