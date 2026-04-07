package com.tetris.tetrisburger_backend.infrastructure.rest.dto.additionsettings;

import java.math.BigDecimal;

public record UpdateAdditionSettingsRequestDTO(
        Integer maxAdditionsPerItem,
        BigDecimal maxTotalPrice,
        boolean additionsEnabled
) {}