package com.tetris.tetrisburger_backend.infrastructure.rest.dto.user;

import com.tetris.tetrisburger_backend.domain.common.ImageStatus;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Integer idUser,
        String userName,
        String email,
        String userImage,
        ImageStatus imageStatus,
        String role,
        String phone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        Integer createdBy,
        Integer updatedBy,
        Integer deletedBy
) {}
