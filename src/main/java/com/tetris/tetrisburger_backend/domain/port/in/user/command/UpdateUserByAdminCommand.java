package com.tetris.tetrisburger_backend.domain.port.in.user.command;

import com.tetris.tetrisburger_backend.domain.model.Role;
import org.springframework.web.multipart.MultipartFile;

public record UpdateUserByAdminCommand(
        Integer idUser,
        String userName,
        String email,
        String password,
        MultipartFile userImage,  // ← Imagen (puede ser null)
        Role role,
        String phone,
        Integer updatedBy         // ← ID del admin que actualiza (auditoría)
) {}
