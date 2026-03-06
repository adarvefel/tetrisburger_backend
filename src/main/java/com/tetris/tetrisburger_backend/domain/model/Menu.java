package com.tetris.tetrisburger_backend.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Menu {

    private Integer idMenu;
    private String name;
    private String description;
    private BigDecimal regularPrice;
    private BigDecimal comboPrice;
    private boolean isAvailable;
    private String imageUrl;
    private String imageKey;
    private Integer idMenuCategory;
    private List<MenuItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Integer createdBy;
    private Integer updatedBy;
    private Integer deletedBy;

    public Menu() {}

    private Menu(Integer idMenu, String name, String description, BigDecimal regularPrice,
                 BigDecimal comboPrice, boolean isAvailable, String imageUrl, String imageKey,
                 Integer idMenuCategory, List<MenuItem> items, LocalDateTime createdAt,
                 LocalDateTime updatedAt, LocalDateTime deletedAt,
                 Integer createdBy, Integer updatedBy, Integer deletedBy) {
        this.idMenu = idMenu;
        this.name = name;
        this.description = description;
        this.regularPrice = regularPrice;
        this.comboPrice = comboPrice;
        this.isAvailable = isAvailable;
        this.imageUrl = imageUrl;
        this.imageKey = imageKey;
        this.idMenuCategory = idMenuCategory;
        this.items = items;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.deletedBy = deletedBy;
    }

    public static Menu create(String name, String description, BigDecimal regularPrice,
                              BigDecimal comboPrice, Boolean isAvailable, String imageUrl,
                              String imageKey, Integer idMenuCategory, List<MenuItem> items,
                              Integer createdBy) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("El nombre del menú es obligatorio");
        if (regularPrice == null || regularPrice.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("El precio regular debe ser mayor a 0");
        if (comboPrice == null || comboPrice.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("El precio combo debe ser mayor a 0");
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("El menú debe tener al menos un ítem");

        Menu menu = new Menu();
        menu.name = name.trim();
        menu.description = description;
        menu.regularPrice = regularPrice;
        menu.comboPrice = comboPrice;
        menu.isAvailable = isAvailable != null ? isAvailable : true;
        menu.imageUrl = imageUrl;
        menu.imageKey = imageKey;
        menu.idMenuCategory = idMenuCategory;
        menu.items = items;
        menu.createdAt = LocalDateTime.now();
        menu.createdBy = createdBy;
        return menu;
    }

    public static Menu reconstitute(Integer idMenu, String name, String description,
                                    BigDecimal regularPrice, BigDecimal comboPrice,
                                    boolean isAvailable, String imageUrl, String imageKey,
                                    Integer idMenuCategory, List<MenuItem> items,
                                    LocalDateTime createdAt, LocalDateTime updatedAt,
                                    LocalDateTime deletedAt, Integer createdBy,
                                    Integer updatedBy, Integer deletedBy) {
        return new Menu(idMenu, name, description, regularPrice, comboPrice, isAvailable,
                imageUrl, imageKey, idMenuCategory, items, createdAt, updatedAt,
                deletedAt, createdBy, updatedBy, deletedBy);
    }

    public void update(String name, String description, BigDecimal regularPrice,
                       BigDecimal comboPrice, Boolean isAvailable, String imageUrl,
                       String imageKey, Integer idMenuCategory, List<MenuItem> items,
                       Integer updatedBy) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("El nombre del menú es obligatorio");
        if (regularPrice == null || regularPrice.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("El precio regular debe ser mayor a 0");
        if (comboPrice == null || comboPrice.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("El precio combo debe ser mayor a 0");
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("El menú debe tener al menos un ítem");

        this.name = name.trim();
        this.description = description;
        this.regularPrice = regularPrice;
        this.comboPrice = comboPrice;
        this.isAvailable = isAvailable != null ? isAvailable : this.isAvailable;
        this.imageUrl = imageUrl;
        this.imageKey = imageKey;
        this.idMenuCategory = idMenuCategory;
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

    public BigDecimal getRegularPrice() { return regularPrice; }
    public void setRegularPrice(BigDecimal regularPrice) { this.regularPrice = regularPrice; }

    public BigDecimal getComboPrice() { return comboPrice; }
    public void setComboPrice(BigDecimal comboPrice) { this.comboPrice = comboPrice; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getImageKey() { return imageKey; }
    public void setImageKey(String imageKey) { this.imageKey = imageKey; }

    public Integer getIdMenuCategory() { return idMenuCategory; }
    public void setIdMenuCategory(Integer idMenuCategory) { this.idMenuCategory = idMenuCategory; }

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