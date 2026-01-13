package com.tetris.tetrisburger_backend.domain.port.in.user.command;

import com.tetris.tetrisburger_backend.domain.model.Role;
import org.springframework.web.multipart.MultipartFile;

public record CreateUserByAdminCommand(

        String userName,
        String email,
        String password,
        MultipartFile userImage,
        Role role,
        String phone,
        Integer createdBy){



}
