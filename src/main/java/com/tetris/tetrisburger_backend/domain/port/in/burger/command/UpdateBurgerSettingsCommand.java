package com.tetris.tetrisburger_backend.domain.port.in.burger.command;

import java.math.BigDecimal;

public record UpdateBurgerSettingsCommand(
        BigDecimal customBurgerMinPrice,
        BigDecimal customBurgerMaxPrice,
        Integer minIngredients,
        Integer maxIngredients,
        Boolean customBurgersEnabled) {}