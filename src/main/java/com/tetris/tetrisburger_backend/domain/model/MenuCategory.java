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

    // Constructor vacío
    public MenuCategory() {
    }

    // Constructor completo
    public MenuCategory(Integer idMenuCategory, String menuCategoryName, String description,
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

    // Constructor simplificado (para factory methods)
    public MenuCategory(Integer idMenuCategory, String menuCategoryName, String description) {
        this.idMenuCategory = idMenuCategory;
        this.menuCategoryName = menuCategoryName;
        this.description = description;
    }

    // Factory method para crear nueva categoría
    public static MenuCategory create(String menuCategoryName, String description) {
        if (menuCategoryName == null || menuCategoryName.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }
        return new MenuCategory(null, menuCategoryName, description);
    }

    // Factory method para reconstitución desde BD
    public static MenuCategory of(Integer idMenuCategory, String menuCategoryName, String description) {
        return new MenuCategory(idMenuCategory, menuCategoryName, description);
    }

    // Método de negocio para actualizar
    public void update(String menuCategoryName, String description) {
        if (menuCategoryName == null || menuCategoryName.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }
        this.menuCategoryName = menuCategoryName;
        this.description = description;
    }

    // Getters y Setters
    public Integer getIdMenuCategory() {
        return idMenuCategory;
    }

    public void setIdMenuCategory(Integer idMenuCategory) {
        this.idMenuCategory = idMenuCategory;
    }

    public String getMenuCategoryName() {
        return menuCategoryName;
    }

    public void setMenuCategoryName(String menuCategoryName) {
        this.menuCategoryName = menuCategoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(Integer deletedBy) {
        this.deletedBy = deletedBy;
    }
}
