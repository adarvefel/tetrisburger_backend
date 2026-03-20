package com.tetris.tetrisburger_backend.domain.model;

import com.tetris.tetrisburger_backend.domain.exception.InvalidSettingsException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BurgerSettings {

    private Integer idSettings;
    private BigDecimal customBurgerMinPrice;
    private BigDecimal customBurgerMaxPrice;
    private Integer minIngredients;
    private Integer maxIngredients;
    private Boolean customBurgersEnabled;

    private Integer updatedBy;
    private LocalDateTime updatedAt;

    private BurgerSettings() {}

    // ============================================
    // FACTORY con defaults
    // ============================================

    public static BurgerSettings createDefaults() {
        BurgerSettings settings = new BurgerSettings();
        settings.idSettings = 1;  // ID único fijo
        settings.customBurgerMinPrice = new BigDecimal("10000.00");
        settings.customBurgerMaxPrice = new BigDecimal("50000.00");
        settings.minIngredients = 2;
        settings.maxIngredients = 15;
        settings.customBurgersEnabled = true;
        settings.updatedAt = LocalDateTime.now();
        return settings;
    }

    // ============================================
    // VALIDACIONES
    // ============================================

    /**
     * Valida precio de burger NUEVA (lanza excepción si no cumple)
     */
    public void validatePriceForNew(BigDecimal price) {
        if (!customBurgersEnabled) {
            throw new InvalidSettingsException(
                    "Las hamburguesas personalizadas están deshabilitadas temporalmente"
            );
        }

        if (price.compareTo(customBurgerMinPrice) < 0) {
            BigDecimal difference = customBurgerMinPrice.subtract(price);
            throw new InvalidSettingsException(
                    String.format(
                            "Tu burger vale $%,.0f COP. El precio mínimo es $%,.0f COP. " +
                                    "Te faltan $%,.0f COP. Agrega más ingredientes.",
                            price, customBurgerMinPrice, difference
                    )
            );
        }

        if (price.compareTo(customBurgerMaxPrice) > 0) {
            BigDecimal difference = price.subtract(customBurgerMaxPrice);
            throw new InvalidSettingsException(
                    String.format(
                            "Tu burger vale $%,.0f COP. El precio máximo es $%,.0f COP. " +
                                    "Excedes por $%,.0f COP. Reduce ingredientes.",
                            price, customBurgerMaxPrice, difference
                    )
            );
        }
    }

    /**
     * Verifica si precio es válido (para burgers viejas - no lanza excepción)
     */
    public boolean isPriceValid(BigDecimal price) {
        if (!customBurgersEnabled) return false;
        return price.compareTo(customBurgerMinPrice) >= 0
                && price.compareTo(customBurgerMaxPrice) <= 0;
    }

    /**
     * Retorna mensaje de por qué no es válida (para burgers viejas)
     */
    public String getInvalidPriceReason(BigDecimal price) {
        if (!customBurgersEnabled) {
            return "️ Las hamburguesas personalizadas están deshabilitadas temporalmente";
        }

        if (price.compareTo(customBurgerMinPrice) < 0) {
            BigDecimal difference = customBurgerMinPrice.subtract(price);
            return String.format(
                    " Tu burger vale $%,.0f COP. El precio mínimo actual es $%,.0f COP. " +
                            "Te faltan $%,.0f COP. Edítala para agregar más ingredientes.",
                    price, customBurgerMinPrice, difference
            );
        }

        if (price.compareTo(customBurgerMaxPrice) > 0) {
            BigDecimal difference = price.subtract(customBurgerMaxPrice);
            return String.format(
                    " Tu burger vale $%,.0f COP. El precio máximo actual es $%,.0f COP. " +
                            "Excedes por $%,.0f COP. Edítala para reducir ingredientes.",
                    price, customBurgerMaxPrice, difference
            );
        }

        return null;
    }

    // ============================================
    // ACTUALIZACIÓN
    // ============================================

    /**
     * Actualiza la configuración
     */
    public void update(
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minIngredients,
            Integer maxIngredients,
            Boolean enabled
    ) {
        validateUpdateData(minPrice, maxPrice, minIngredients, maxIngredients);

        if (minPrice != null) this.customBurgerMinPrice = minPrice;
        if (maxPrice != null) this.customBurgerMaxPrice = maxPrice;
        if (minIngredients != null) this.minIngredients = minIngredients;
        if (maxIngredients != null) this.maxIngredients = maxIngredients;
        if (enabled != null) this.customBurgersEnabled = enabled;

        this.updatedAt = LocalDateTime.now();
    }

    private void validateUpdateData(BigDecimal minPrice, BigDecimal maxPrice,
                                    Integer minIngredients, Integer maxIngredients) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new InvalidSettingsException(
                    "El precio mínimo no puede ser mayor al máximo"
            );
        }
        if (minIngredients != null && maxIngredients != null && minIngredients > maxIngredients) {
            throw new InvalidSettingsException(
                    "El mínimo de ingredientes no puede ser mayor al máximo"
            );
        }
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidSettingsException(
                    "El precio mínimo debe ser mayor a 0"
            );
        }
    }

    // ============================================
    // GETTERS
    // ============================================

    public Integer getIdSettings() { return idSettings; }
    public BigDecimal getCustomBurgerMinPrice() { return customBurgerMinPrice; }
    public BigDecimal getCustomBurgerMaxPrice() { return customBurgerMaxPrice; }
    public Integer getMinIngredients() { return minIngredients; }
    public Integer getMaxIngredients() { return maxIngredients; }
    public Boolean isCustomBurgersEnabled() { return customBurgersEnabled; }
    public Integer getUpdatedBy() { return updatedBy; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // ============================================
    // SETTERS (para reconstitución)
    // ============================================

    public void setIdSettings(Integer id) { this.idSettings = id; }
}
