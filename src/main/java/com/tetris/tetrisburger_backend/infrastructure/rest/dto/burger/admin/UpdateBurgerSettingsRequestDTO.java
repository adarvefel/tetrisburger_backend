package com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.admin;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record UpdateBurgerSettingsRequestDTO(

        @NotNull(message = "El precio mínimo es obligatorio")
        @DecimalMin(value = "1000.00", message = "El precio mínimo debe ser al menos $1,000")
        @DecimalMax(value = "100000.00", message = "El precio mínimo no puede exceder $100,000")
        BigDecimal customBurgerMinPrice,

        @NotNull(message = "El precio máximo es obligatorio")
        @DecimalMin(value = "10000.00", message = "El precio máximo debe ser al menos $10,000")
        @DecimalMax(value = "200000.00", message = "El precio máximo no puede exceder $200,000")
        BigDecimal customBurgerMaxPrice,

        @NotNull(message = "El mínimo de ingredientes es obligatorio")
        @Min(value = 1, message = "Debe haber al menos 1 ingrediente mínimo")
        @Max(value = 20, message = "El mínimo de ingredientes no puede exceder 20")
        Integer minIngredients,

        @NotNull(message = "El máximo de ingredientes es obligatorio")
        @Min(value = 1, message = "Debe haber al menos 1 ingrediente máximo")
        @Max(value = 20, message = "El máximo de ingredientes no puede exceder 20")
        Integer maxIngredients,

        @NotNull(message = "El estado de burgers personalizadas es obligatorio")
        Boolean customBurgersEnabled
) {}
