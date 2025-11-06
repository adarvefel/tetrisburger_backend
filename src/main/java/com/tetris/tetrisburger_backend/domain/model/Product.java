package com.tetris.tetrisburger_backend.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Product {
    private Integer id;
    private String name;
    private String description;
    private Integer quantity;
    private BigDecimal price;
    private boolean availability;
    private String productType;
    private String ingredientType;
    private boolean burgerIngredient;
    private Integer productCategoryId;
    private Integer supplierId;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private Integer createdBy;
    private Integer updatedBy;
    private Integer deletedBy;

    public Product(Integer id, String name, String description, Integer quantity, BigDecimal price,
                   boolean availability, String productType, String ingredientType, boolean burgerIngredient,
                   Integer productCategoryId, Integer supplierId, Instant createdAt, Instant updatedAt,
                   Instant deletedAt, Integer createdBy, Integer updatedBy, Integer deletedBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.availability = availability;
        this.productType = productType;
        this.ingredientType = ingredientType;
        this.burgerIngredient = burgerIngredient;
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
                                boolean availability, String productType, String ingredientType, boolean burgerIngredient,
                                Integer productCategoryId, Integer supplierId, Integer createdBy) {
        return new Product(null, name, description, quantity, price, availability, productType, ingredientType,
                burgerIngredient, productCategoryId, supplierId, Instant.now(), null, null, createdBy, null, null);
    }

    // getters y setters
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

    public boolean isAvailability() {
        return availability;
    }

    public String getProductType() {
        return productType;
    }

    public String getIngredientType() {
        return ingredientType;
    }

    public boolean isBurgerIngredient() {
        return burgerIngredient;
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

    public void setId(Integer id) {
        this.id = id;
    }
}
