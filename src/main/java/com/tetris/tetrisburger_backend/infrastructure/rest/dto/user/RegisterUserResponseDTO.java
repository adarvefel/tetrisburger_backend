package com.tetris.tetrisburger_backend.infrastructure.rest.dto.user;

import java.time.LocalDateTime;

public record RegisterUserResponseDTO(
        Integer idUser,
        String userName,
        String email,
        String userImage,
        String phone,
        LocalDateTime createdAt

) {
}
