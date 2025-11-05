package com.tetris.tetrisburger_backend.domain.port.in.user.command;

public record UpdateProfileUserCommand(
        Integer idUser,
    String userName,
    String password,
        String userImage,
    String phone){


}
