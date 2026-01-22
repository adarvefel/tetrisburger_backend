// src/main/java/com/tetris/tetrisburger_backend/domain/model/Product.java
package com.tetris.tetrisburger_backend.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Product {
    private Integer id;
    private String name;
    private String description;
    private Integer quantity;
    private BigDecimal price;
    private Boolean availability;
    private String productType;
    private String ingredientType;
    private Boolean burgerIngredient;
    private String imageUrl; // nuevo
    private Integer productCategoryId;
    private Integer supplierId;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private Integer createdBy;
    private Integer updatedBy;
    private Integer deletedBy;

    private Product(Integer id, String name, String description, Integer quantity, BigDecimal price,
                    Boolean availability, String productType, String ingredientType, Boolean burgerIngredient,
                    String imageUrl, Integer productCategoryId, Integer supplierId,
                    Instant createdAt, Instant updatedAt, Instant deletedAt,
                    Integer createdBy, Integer updatedBy, Integer deletedBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.availability = availability;
        this.productType = productType;
        this.ingredientType = ingredientType;
        this.burgerIngredient = burgerIngredient;
        this.imageUrl = imageUrl;
        this.productCategoryId = productCategoryId;
        this.supplierId = supplierId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.deletedBy = deletedBy;
    }

    public static Product ofNew(String name, String description, Integer quantity, BigDecimal price,
                                Boolean availability, String productType, String ingredientType, Boolean burgerIngredient,
                                String imageUrl, Integer productCategoryId, Integer supplierId, Integer createdBy) {
        return new Product(null, name, description, quantity, price, availability, productType, ingredientType,
                burgerIngredient, imageUrl, productCategoryId, supplierId, null, null, null, createdBy, null, null);
    }

    public static Product of(Integer id, String name, String description, Integer quantity, BigDecimal price,
                             Boolean availability, String productType, String ingredientType, Boolean burgerIngredient,
                             String imageUrl, Integer productCategoryId, Integer supplierId,
                             Instant createdAt, Instant updatedAt, Instant deletedAt,
                             Integer createdBy, Integer updatedBy, Integer deletedBy) {
        return new Product(id, name, description, quantity, price, availability, productType, ingredientType,
                burgerIngredient, imageUrl, productCategoryId, supplierId, createdAt, updatedAt, deletedAt,
                createdBy, updatedBy, deletedBy);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public String getProductType() {
        return productType;
    }

    public String getIngredientType() {
        return ingredientType;
    }

    public Boolean getBurgerIngredient() {
        return burgerIngredient;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getProductCategoryId() {
        return productCategoryId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public Integer getDeletedBy() {
        return deletedBy;
    }
}
