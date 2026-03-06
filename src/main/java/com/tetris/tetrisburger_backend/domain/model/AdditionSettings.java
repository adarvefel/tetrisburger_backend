package com.tetris.tetrisburger_backend.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdditionSettings {

    private Integer idSettings;
    private Integer maxAdditionsPerItem;
    private BigDecimal maxTotalPrice;
    private boolean additionsEnabled;
    private LocalDateTime updatedAt;

    public AdditionSettings() {}

    private AdditionSettings(Integer idSettings, Integer maxAdditionsPerItem,
                             BigDecimal maxTotalPrice, boolean additionsEnabled,
                             LocalDateTime updatedAt) {
        this.idSettings = idSettings;
        this.maxAdditionsPerItem = maxAdditionsPerItem;
        this.maxTotalPrice = maxTotalPrice;
        this.additionsEnabled = additionsEnabled;
        this.updatedAt = updatedAt;
    }

    public static AdditionSettings reconstitute(Integer idSettings, Integer maxAdditionsPerItem,
                                                BigDecimal maxTotalPrice, boolean additionsEnabled,
                                                LocalDateTime updatedAt) {
        return new AdditionSettings(idSettings, maxAdditionsPerItem, maxTotalPrice,
                additionsEnabled, updatedAt);
    }

    public void update(Integer maxAdditionsPerItem, BigDecimal maxTotalPrice, boolean additionsEnabled) {
        this.maxAdditionsPerItem = maxAdditionsPerItem;
        this.maxTotalPrice = maxTotalPrice;
        this.additionsEnabled = additionsEnabled;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Integer getIdSettings() { return idSettings; }
    public void setIdSettings(Integer idSettings) { this.idSettings = idSettings; }

    public Integer getMaxAdditionsPerItem() { return maxAdditionsPerItem; }
    public void setMaxAdditionsPerItem(Integer maxAdditionsPerItem) { this.maxAdditionsPerItem = maxAdditionsPerItem; }

    public BigDecimal getMaxTotalPrice() { return maxTotalPrice; }
    public void setMaxTotalPrice(BigDecimal maxTotalPrice) { this.maxTotalPrice = maxTotalPrice; }

    public boolean isAdditionsEnabled() { return additionsEnabled; }
    public void setAdditionsEnabled(boolean additionsEnabled) { this.additionsEnabled = additionsEnabled; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}