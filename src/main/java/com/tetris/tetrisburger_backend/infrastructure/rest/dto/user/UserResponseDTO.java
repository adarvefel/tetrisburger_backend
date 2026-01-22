package com.tetris.tetrisburger_backend.infrastructure.rest.dto.user;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Integer idUser,
        String userName,
        String email,
        String userImage,
        String imageStatus,
        String role,
        String phone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        Integer createdBy,
        Integer updatedBy,
        Integer deletedBy
) {}
