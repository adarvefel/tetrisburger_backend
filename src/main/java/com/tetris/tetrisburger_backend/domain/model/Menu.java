package com.tetris.tetrisburger_backend.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class Menu {

    private Integer idMenu;
    private String name;
    private String description;
    private boolean isAvailable;
    private String imageUrl;
    private String imageKey;
    private MenuCategory menuCategory;
    private List<MenuItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Integer createdBy;
    private Integer updatedBy;
    private Integer deletedBy;

    public Menu() {}

    private Menu(Integer idMenu, String name, String description, boolean isAvailable,
                 String imageUrl, String imageKey, MenuCategory menuCategory,
                 List<MenuItem> items, LocalDateTime createdAt, LocalDateTime updatedAt,
                 LocalDateTime deletedAt, Integer createdBy, Integer updatedBy, Integer deletedBy) {
        this.idMenu = idMenu;
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
        this.imageUrl = imageUrl;
        this.imageKey = imageKey;
        this.menuCategory = menuCategory;
        this.items = items;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.deletedBy = deletedBy;
    }

    public static Menu create(String name, String description, Boolean isAvailable,
                              String imageUrl, String imageKey, MenuCategory menuCategory,
                              List<MenuItem> items, Integer createdBy) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("El nombre del menú es obligatorio");
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("El menú debe tener al menos un ítem");

        Menu menu = new Menu();
        menu.name = name.trim();
        menu.description = description;
        menu.isAvailable = isAvailable != null ? isAvailable : true;
        menu.imageUrl = imageUrl;
        menu.imageKey = imageKey;
        menu.menuCategory = menuCategory;
        menu.items = items;
        menu.createdAt = LocalDateTime.now();
        menu.createdBy = createdBy;
        return menu;
    }

    public static Menu reconstitute(Integer idMenu, String name, String description,
                                    boolean isAvailable, String imageUrl, String imageKey,
                                    MenuCategory menuCategory, List<MenuItem> items,
                                    LocalDateTime createdAt, LocalDateTime updatedAt,
                                    LocalDateTime deletedAt, Integer createdBy,
                                    Integer updatedBy, Integer deletedBy) {
        return new Menu(idMenu, name, description, isAvailable, imageUrl, imageKey,
                menuCategory, items, createdAt, updatedAt, deletedAt,
                createdBy, updatedBy, deletedBy);
    }

    public void update(String name, String description, Boolean isAvailable, MenuCategory menuCategory,
                       List<MenuItem> items, Integer updatedBy) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("El nombre del menú es obligatorio");
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("El menú debe tener al menos un ítem");

        this.name = name.trim();
        this.description = description;
        this.isAvailable = isAvailable != null ? isAvailable : this.isAvailable;
        this.menuCategory = menuCategory;
        this.items = items;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    public void updateImage(String imageUrl, String imageKey) {
        this.imageUrl = imageUrl;
        this.imageKey = imageKey;
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete(Integer deletedBy) {
        if (this.deletedAt != null)
            throw new IllegalStateException("El menú ya fue eliminado");
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    public void toggleAvailability() {
        this.isAvailable = !this.isAvailable;
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== GETTERS Y SETTERS ====================

    public Integer getIdMenu() { return idMenu; }
    public void setIdMenu(Integer idMenu) { this.idMenu = idMenu; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getImageKey() { return imageKey; }
    public void setImageKey(String imageKey) { this.imageKey = imageKey; }

    public MenuCategory getMenuCategory() { return menuCategory; }
    public void setMenuCategory(MenuCategory menuCategory) { this.menuCategory = menuCategory; }

    public List<MenuItem> getItems() { return items; }
    public void setItems(List<MenuItem> items) { this.items = items; }

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