package com.tetris.tetrisburger_backend.domain.model;

import java.time.LocalDateTime;

public class MenuCategory {
    private Integer idMenuCategory;
    private String menuCategoryName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Integer createdBy;
    private Integer updatedBy;
    private Integer deletedBy;

    public MenuCategory() {}

    private MenuCategory(Integer idMenuCategory, String menuCategoryName, String description,
                         LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt,
                         Integer createdBy, Integer updatedBy, Integer deletedBy) {
        this.idMenuCategory = idMenuCategory;
        this.menuCategoryName = menuCategoryName;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.deletedBy = deletedBy;
    }

    // ==================== FACTORY METHODS ====================

    public static MenuCategory create(String menuCategoryName, String description, Integer createdBy) {
        if (menuCategoryName == null || menuCategoryName.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }
        MenuCategory mc = new MenuCategory();
        mc.menuCategoryName = menuCategoryName.trim();
        mc.description = description;
        mc.createdAt = LocalDateTime.now();
        mc.createdBy = createdBy;
        return mc;
    }

    public static MenuCategory reconstitute(Integer idMenuCategory, String menuCategoryName,
                                            String description, LocalDateTime createdAt,
                                            LocalDateTime updatedAt, LocalDateTime deletedAt,
                                            Integer createdBy, Integer updatedBy, Integer deletedBy) {
        return new MenuCategory(idMenuCategory, menuCategoryName, description,
                createdAt, updatedAt, deletedAt, createdBy, updatedBy, deletedBy);
    }

    // ==================== MÉTODOS DE NEGOCIO ====================

    public void update(String menuCategoryName, String description, Integer updatedBy) {
        if (menuCategoryName == null || menuCategoryName.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }
        this.menuCategoryName = menuCategoryName.trim();
        this.description = description;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    public void softDelete(Integer deletedBy) {
        if (this.deletedAt != null) {
            throw new IllegalStateException("La categoría ya fue eliminada");
        }
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    // ==================== GETTERS Y SETTERS ====================

    public Integer getIdMenuCategory() { return idMenuCategory; }
    public void setIdMenuCategory(Integer idMenuCategory) { this.idMenuCategory = idMenuCategory; }

    public String getMenuCategoryName() { return menuCategoryName; }
    public void setMenuCategoryName(String menuCategoryName) { this.menuCategoryName = menuCategoryName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public Integer getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Integer updatedBy) { this.updatedBy = updatedBy; }

    public Integer getDeletedBy() { return deletedBy; }
    public void setDeletedBy(Integer deletedBy) { this.deletedBy = deletedBy; }
}