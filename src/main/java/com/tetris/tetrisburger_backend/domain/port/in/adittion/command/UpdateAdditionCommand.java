package com.tetris.tetrisburger_backend.domain.port.in.adittion.command;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateAdditionCommand(
        Integer id,
        String name,
        String description,
        BigDecimal price,
        Boolean available
) {
}
