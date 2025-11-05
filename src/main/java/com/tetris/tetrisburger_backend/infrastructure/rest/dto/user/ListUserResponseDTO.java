package com.tetris.tetrisburger_backend.infrastructure.rest.dto.user;

import java.time.LocalDateTime;
import java.util.List;

public record ListUserResponseDTO(
        List<UserResponseDTO> users,
        Long totalElements,
        Integer totalPages,
        LocalDateTime timestamp
) {}
