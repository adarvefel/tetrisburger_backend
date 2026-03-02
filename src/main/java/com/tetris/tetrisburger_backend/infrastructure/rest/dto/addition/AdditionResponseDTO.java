package com.tetris.tetrisburger_backend.infrastructure.rest.dto.addition;

import com.tetris.tetrisburger_backend.domain.common.ImageStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdditionResponseDTO(
        Integer idAddition,
        String name,
        String description,
        BigDecimal price,
        Boolean available,
        String imageUrl,
        String imageKey,
        ImageStatus ImageStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {}
