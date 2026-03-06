package com.tetris.tetrisburger_backend.infrastructure.rest.dto.additionsettings;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdditionSettingsResponseDTO(
        Integer idSettings,
        Integer maxAdditionsPerItem,
        BigDecimal maxTotalPrice,
        boolean additionsEnabled,
        LocalDateTime updatedAt
) {}