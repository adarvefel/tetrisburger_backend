package com.tetris.tetrisburger_backend.domain.port.in.adittion.command;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateAdditionCommand(
        String name,
        String description,
        BigDecimal price,
        Boolean available,
        FileData additionImage
) {
}
