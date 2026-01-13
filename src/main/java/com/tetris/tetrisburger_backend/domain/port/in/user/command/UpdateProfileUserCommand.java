package com.tetris.tetrisburger_backend.domain.port.in.user.command;

import org.springframework.web.multipart.MultipartFile;

public record UpdateProfileUserCommand(
        Integer idUser,
        String userName,
        String password,
        MultipartFile userImage,
        String phone
){


}
