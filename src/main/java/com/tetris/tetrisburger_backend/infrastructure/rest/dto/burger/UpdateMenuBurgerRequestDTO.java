// src/main/java/com/tetris/tetrisburger_backend/infrastructure/rest/dto/burger/UpdateMenuBurgerRequestDTO.java
package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UpdateMenuBurgerRequestDTO(
        String name,
        String description,
        List<IngredientRequestDTO> ingredients,
        Boolean availability,
        Boolean isFavorite,
        Boolean isOnMenu
) {
}
