package com.tetris.tetrisburger_backend.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Addition {
    private Integer idAddition;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean available;
    private String imageUrl;
    private String imageKey;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt; // ✅ soft delete

    // ==================== CONSTRUCTORES ====================

    private Addition() {}

    private Addition(
            Integer idAddition,
            String name,
            String description,
            BigDecimal price,
            Boolean available,
            String imageUrl,
            String imageKey,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt
    ) {
        this.idAddition = idAddition;
        this.name = name;
        this.description = description;
        this.price = price;
        this.available = available;
        this.imageUrl = imageUrl;
        this.imageKey = imageKey;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    // ==================== FACTORIES ====================

    public static Addition create(
            String name,
            String description,
            BigDecimal price,
            Boolean available,
            String imageUrl
    ) {
        return create(name, description, price, available, imageUrl, null);
    }

    public static Addition create(
            String name,
            String description,
            BigDecimal price,
            Boolean available,
            String imageUrl,
            String imageKey
    ) {
        validateName(name);
        validatePrice(price);

        LocalDateTime now = LocalDateTime.now();

        return new Addition(
                null,
                name.trim(),
                description != null ? description.trim() : null,
                price,
                available != null ? available : true,
                imageUrl != null ? imageUrl.trim() : null,
                imageKey != null ? imageKey.trim() : null,
                now,
                null,
                null // deletedAt = null en creación
        );
    }

    // Factory completo (reconstrucción desde BD)
    public static Addition of(
            Integer idAddition,
            String name,
            String description,
            BigDecimal price,
            Boolean available,
            String imageUrl,
            String imageKey,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt
    ) {
        return new Addition(
                idAddition, name, description, price, available,
                imageUrl, imageKey, createdAt, updatedAt, deletedAt
        );
    }

    // Overload sin deletedAt (compatibilidad)
    public static Addition of(
            Integer idAddition,
            String name,
            String description,
            BigDecimal price,
            Boolean available,
            String imageUrl,
            String imageKey,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Addition(
                idAddition, name, description, price, available,
                imageUrl, imageKey, createdAt, updatedAt, null
        );
    }

    // ==================== COMPORTAMIENTO ====================

    public void update(
            String name,
            String description,
            BigDecimal price,
            Boolean available,
            String imageUrl
    ) {
        validateName(name);
        validatePrice(price);

        this.name = name.trim();
        this.description = description != null ? description.trim() : null;
        this.price = price;

        if (available != null) {
            this.available = available;
        }

        if (imageUrl != null) {
            this.imageUrl = imageUrl.trim();
        }

        this.updatedAt = LocalDateTime.now();
    }

    public void updateImage(String imageKey, String imageUrl) {
        this.imageKey = imageKey != null ? imageKey.trim() : null;
        this.imageUrl = imageUrl != null ? imageUrl.trim() : null;
        this.updatedAt = LocalDateTime.now();
    }

    public void enable() {
        this.available = true;
        this.updatedAt = LocalDateTime.now();
    }



    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
        this.available = false;
        this.updatedAt = LocalDateTime.now();
    }


    // ==================== CONSULTAS ====================

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(this.available) && !isDeleted();
    }

    // ==================== VALIDACIONES ====================

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }
        if (name.length() > 150) {
            throw new IllegalArgumentException("El nombre no puede exceder 150 caracteres");
        }
    }

    private static void validatePrice(BigDecimal price) {
        if (price == null) throw new IllegalArgumentException("El precio es requerido");
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
    }

    // ==================== GETTERS ====================

    public Integer getIdAddition() { return idAddition; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public Boolean getAvailable() { return available; }
    public String getImageUrl() { return imageUrl; }
    public String getImageKey() { return imageKey; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; } // ✅
}
