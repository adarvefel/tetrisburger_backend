package com.tetris.tetrisburger_backend.infrastructure.rest.dto.user;

import java.time.LocalDateTime;

public record UpdateUserByAdminResponseDTO(
        Integer idUser,
        String userName,
        String email,
        String userImage,
        String role,
        String phone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer createdBy,
        Integer updatedBy



        ) {
}
