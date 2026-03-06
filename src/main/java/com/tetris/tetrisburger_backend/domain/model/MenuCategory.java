package com.tetris.tetrisburger_backend.domain.model;

import java.time.LocalDateTime;

public class MenuCategory {
    private Integer idMenuCategory;
    private String menuCategoryName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public MenuCategory() {
    }

    public MenuCategory(Integer idMenuCategory, String menuCategoryName, String description,
                        LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.idMenuCategory = idMenuCategory;
        this.menuCategoryName = menuCategoryName;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    // Factory: nueva categoría
    public static MenuCategory create(String menuCategoryName, String description) {
        if (menuCategoryName == null || menuCategoryName.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }
        MenuCategory mc = new MenuCategory();
        mc.menuCategoryName = menuCategoryName.trim();
        mc.description = description;
        mc.createdAt = LocalDateTime.now();
        return mc;
    }

    // Factory: reconstituir desde DB
    public static MenuCategory reconstitute(Integer idMenuCategory, String menuCategoryName,
                                            String description, LocalDateTime createdAt, LocalDateTime updatedAt,
                                            LocalDateTime deletedAt) {
        return new MenuCategory(idMenuCategory, menuCategoryName, description,
                createdAt, updatedAt, deletedAt);
    }

    // Método de negocio
    public void update(String menuCategoryName, String description) {
        if (menuCategoryName == null || menuCategoryName.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }
        this.menuCategoryName = menuCategoryName.trim();
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        if (this.deletedAt != null) {
            throw new IllegalStateException("La categoría ya fue eliminada");
        }
        this.deletedAt = LocalDateTime.now();
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
}
