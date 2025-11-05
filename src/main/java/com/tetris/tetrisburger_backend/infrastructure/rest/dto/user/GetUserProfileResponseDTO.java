package com.tetris.tetrisburger_backend.infrastructure.rest.dto.user;

import java.time.LocalDateTime;

public record GetUserProfileResponseDTO(
    Integer idUser,
    String userName,
    String email,
    String userImage,
    String role,
    String phone,
    LocalDateTime createdAt
){
}
