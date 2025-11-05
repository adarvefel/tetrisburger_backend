package com.tetris.tetrisburger_backend.infrastructure.rest.dto.user;

import java.time.LocalDateTime;

public record UpdateProfileUserResponseDTO(
        Integer idUser,
        String userName,
        String email,
        String userImage,
        String phone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer createdBy,
        Integer updatedBy
) {
}
