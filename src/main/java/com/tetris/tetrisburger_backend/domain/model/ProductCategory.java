package com.tetris.tetrisburger_backend.domain.model;

import java.time.LocalDateTime;

public class ProductCategory {

    private Integer id;
    private String name;
    private String description;
    private Boolean available;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Integer createdBy;
    private Integer updatedBy;
    private Integer deletedBy;

    private ProductCategory() {}

    public static ProductCategory ofNew(String name, String description,
                                        Boolean available, Integer createdBy) {
        ProductCategory pc = new ProductCategory();
        pc.name        = name;
        pc.description = description;
        pc.available   = available != null ? available : Boolean.TRUE;
        pc.createdAt   = LocalDateTime.now();
        pc.createdBy   = createdBy;
        return pc;
    }

    public static ProductCategory reconstitute(
            Integer id, String name, String description, Boolean available,
            LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt,
            Integer createdBy, Integer updatedBy, Integer deletedBy
    ) {
        ProductCategory pc = new ProductCategory();
        pc.id          = id;
        pc.name        = name;
        pc.description = description;
        pc.available   = available != null ? available : Boolean.TRUE;
        pc.createdAt   = createdAt;
        pc.updatedAt   = updatedAt;
        pc.deletedAt   = deletedAt;
        pc.createdBy   = createdBy;
        pc.updatedBy   = updatedBy;
        pc.deletedBy   = deletedBy;
        return pc;
    }

    // ── Actualización con auditoría ──────────────────────────────────────────
    public void update(String name, String description,
                       Boolean available, Integer updatedBy) {
        this.name        = name;
        this.description = description;
        this.available   = available != null ? available : Boolean.TRUE;
        this.updatedAt   = LocalDateTime.now();
        this.updatedBy   = updatedBy;
    }

    // ── Soft delete (HT-02) ──────────────────────────────────────────────────
    public void softDelete(Integer deletedBy) {
        if (this.deletedAt != null)
            throw new IllegalStateException("La categoría ya fue eliminada");
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = deletedBy;
    }

    public boolean isDeleted() { return this.deletedAt != null; }

    // ── Getters ──────────────────────────────────────────────────────────────
    public Integer getId()             { return id; }
    public String getName()            { return name; }
    public String getDescription()     { return description; }
    public Boolean getAvailable()      { return available; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
    public LocalDateTime getUpdatedAt(){ return updatedAt; }
    public LocalDateTime getDeletedAt(){ return deletedAt; }
    public Integer getCreatedBy()      { return createdBy; }
    public Integer getUpdatedBy()      { return updatedBy; }
    public Integer getDeletedBy()      { return deletedBy; }

    // ── Setter mínimo ────────────────────────────────────────────────────────
    public void setAvailable(Boolean available) { this.available = available; }
}