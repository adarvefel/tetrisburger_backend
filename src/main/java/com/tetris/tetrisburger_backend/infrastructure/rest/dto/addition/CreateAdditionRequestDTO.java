// src/main/java/com/tetris/tetrisburger_backend/infrastructure/rest/dto/addition/CreateAdditionRequestDto.java
package com.tetris.tetrisburger_backend.infrastructure.rest.dto.addition;

import java.math.BigDecimal;

public record CreateAdditionRequestDTO(
        String name,
        String description,
        BigDecimal price,
        Boolean available
) {}
