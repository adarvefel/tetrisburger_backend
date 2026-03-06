package com.tetris.tetrisburger_backend.domain.port.in.additionsettings.command;

import java.math.BigDecimal;

public record UpdateAdditionSettingsCommand(
        Integer maxAdditionsPerItem,
        BigDecimal maxTotalPrice,
        boolean additionsEnabled


) {
}
