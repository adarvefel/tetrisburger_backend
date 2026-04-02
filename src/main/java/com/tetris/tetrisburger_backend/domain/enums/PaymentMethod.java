package com.tetris.tetrisburger_backend.domain.enums;


public enum PaymentMethod {
    CASH("Efectivo"),
    CARD("Tarjeta"),
    TRANSFER("Transferencia");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}