// src/main/java/com/tetris/tetrisburger_backend/infrastructure/rest/dto/addition/UpdateAdditionRequestDto.java
package com.tetris.tetrisburger_backend.infrastructure.rest.dto.addition;

import java.math.BigDecimal;

public record UpdateAdditionRequestDTO(
        String name,
        String description,
        BigDecimal price,
        Boolean available
) {
    public UpdateAdditionRequestDTO(String name, double price, Boolean available) {
        this(name, null, BigDecimal.valueOf(price), available);
    }
}
