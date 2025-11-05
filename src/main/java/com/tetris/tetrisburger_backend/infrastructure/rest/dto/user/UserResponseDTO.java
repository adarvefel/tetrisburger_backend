package com.tetris.tetrisburger_backend.infrastructure.rest.dto.user;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Integer idUser,
        String userName,
        String email,
        String userImage,
        String role,
        String phone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deleteAt,
        Integer createdBy,
        Integer updatedBy,
        Integer deletedBy
) {}
