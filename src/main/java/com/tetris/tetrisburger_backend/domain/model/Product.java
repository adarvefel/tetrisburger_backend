package com.tetris.tetrisburger_backend.domain.model;

import com.tetris.tetrisburger_backend.domain.enums.ProductType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {
    private Integer id;
    private String name;
    private String description;
    private Integer quantity;
    private BigDecimal price;
    private Boolean availability;
    private ProductType productType;
    private ProductCategory productCategory;
    private String imageUrl;
    private String imageKey;
    private Supplier supplier;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Integer createdBy;
    private Integer updatedBy;
    private Integer deletedBy;

    // ==================== CONSTRUCTORES ====================

    private Product() {}

    private Product(Integer id, String name, String description, Integer quantity, BigDecimal price,
                    Boolean availability, ProductType productType,
                    ProductCategory productCategory, String imageUrl, String imageKey, Supplier supplier,
                    LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt,
                    Integer createdBy, Integer updatedBy, Integer deletedBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.availability = availability;
        this.productType = productType;
        this.productCategory = productCategory;
        this.imageUrl = imageUrl;
        this.imageKey = imageKey;
        this.supplier = supplier;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.deletedBy = deletedBy;
    }

    // ==================== FACTORY METHODS ====================

    public static Product of(Integer id, String name, String description, Integer quantity, BigDecimal price,
                             Boolean availability, ProductType productType,
                             ProductCategory productCategory, String imageUrl, String imageKey, Supplier supplier,
                             LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt,
                             Integer createdBy, Integer updatedBy, Integer deletedBy) {
        return new Product(id, name, description, quantity, price, availability, productType,
                productCategory, imageUrl, imageKey, supplier,
                createdAt, updatedAt, deletedAt, createdBy, updatedBy, deletedBy);
    }

    public static Product create(
            String name,
            String description,
            Integer quantity,
            BigDecimal price,
            Boolean availability,
            ProductType productType,
            ProductCategory productCategory,
            String imageUrl,
            String imageKey,
            Supplier supplier,
            Integer createdBy
    ) {
        validateName(name);
        validateQuantity(quantity);
        validatePrice(price);
        validateProductType(productType);
        validateProductCategory(productCategory);
        validateSupplier(supplier);

        Product product = new Product();
        product.name = name;
        product.description = description;
        product.quantity = quantity;
        product.price = price;
        product.availability = availability != null ? availability : true;
        product.productType = productType;
        product.productCategory = productCategory;
        product.imageUrl = imageUrl;
        product.imageKey = imageKey;
        product.supplier = supplier;
        product.createdAt = LocalDateTime.now();
        product.createdBy = createdBy;

        return product;
    }

    // ==================== VALIDACIONES ====================

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es requerido");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("El nombre no puede exceder 100 caracteres");
        }
    }

    private static void validateQuantity(Integer quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException("La cantidad es requerida");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
    }

    private static void validatePrice(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("El precio es requerido");
        }
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
    }

    private static void validateProductType(ProductType productType) {
        if (productType == null) {
            throw new IllegalArgumentException("El tipo de producto es requerido");
        }
    }

    private static void validateProductCategory(ProductCategory productCategory) {
        if (productCategory == null) {
            throw new IllegalArgumentException("La categoría es requerida");
        }
    }

    private static void validateSupplier(Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("El proveedor es requerido");
        }
    }

    // ==================== MÉTODOS DE ACTUALIZACIÓN ====================

    public void updateDetails(
            String name,
            String description,
            Integer quantity,
            BigDecimal price,
            Boolean availability,
            ProductType productType,
            ProductCategory productCategory,
            Supplier supplier,
            Integer updatedBy
    ) {
        validateName(name);
        validateQuantity(quantity);
        validatePrice(price);
        validateProductType(productType);
        validateProductCategory(productCategory);
        validateSupplier(supplier);

        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.availability = availability != null ? availability : this.availability;
        this.productType = productType;
        this.productCategory = productCategory;
        this.supplier = supplier;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    public void updateImage(String imageKey, String imageUrl, Integer updatedBy) {
        this.imageKey = imageKey;
        this.imageUrl = imageUrl;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    public void updateName(String name, Integer updatedBy) {
        validateName(name);
        this.name = name;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    public void updateDescription(String description, Integer updatedBy) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    public void updatePrice(BigDecimal price, Integer updatedBy) {
        validatePrice(price);
        this.price = price;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    public void updateQuantity(Integer newQuantity, Integer updatedBy) {
        validateQuantity(newQuantity);
        this.quantity = newQuantity;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    public void adjustStock(int delta, Integer updatedBy) {
        int newQuantity = this.quantity + delta;
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Stock insuficiente. Stock actual: " + this.quantity);
        }
        this.quantity = newQuantity;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    public void updateAvailability(Boolean availability, Integer updatedBy) {
        this.availability = availability;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    // ==================== MÉTODOS DE CONSULTA ====================

    public boolean isAvailable() {
        return Boolean.TRUE.equals(this.availability) && this.deletedAt == null;
    }

    public boolean hasStock() {
        return quantity > 0;
    }

    public boolean canBeSold() {
        return isAvailable() && hasStock();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public boolean hasImage() {
        return this.imageKey != null && !this.imageKey.isEmpty();
    }

    public boolean hasCategory() {
        return this.productCategory != null;
    }

    public boolean isIngredient() {
        return ProductType.INGREDIENT.equals(this.productType);
    }

    public boolean isBeverage() {
        return ProductType.BEVERAGE.equals(this.productType);
    }

    public boolean isSide() {
        return ProductType.SIDE.equals(this.productType);
    }

    // ✅ Calculado desde productType, sin campo redundante
    public boolean isBurgerIngredient() {
        return ProductType.INGREDIENT.equals(this.productType);
    }

    // ==================== HELPERS ====================

    public String getCategoryName() {
        return this.productCategory != null ? this.productCategory.getName() : null;
    }

    public Integer getCategoryId() {
        return this.productCategory != null ? this.productCategory.getId() : null;
    }

    // ==================== GETTERS ====================

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
    public Boolean getAvailability() { return availability; }
    public ProductType getProductType() { return productType; }
    public ProductCategory getProductCategory() { return productCategory; }
    public Supplier getSupplier() { return supplier; }
    public String getImageUrl() { return imageUrl; }
    public String getImageKey() { return imageKey; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public Integer getCreatedBy() { return createdBy; }
    public Integer getUpdatedBy() { return updatedBy; }
    public Integer getDeletedBy() { return deletedBy; }

    // ==================== SETTERS ====================

    void setId(Integer id) { this.id = id; }
    void setName(String name) { this.name = name; }
    void setDescription(String description) { this.description = description; }
    void setQuantity(Integer quantity) { this.quantity = quantity; }
    void setPrice(BigDecimal price) { this.price = price; }
    void setAvailability(Boolean availability) { this.availability = availability; }
    void setProductType(ProductType productType) { this.productType = productType; }
    void setProductCategory(ProductCategory productCategory) { this.productCategory = productCategory; }
    void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    void setImageKey(String imageKey) { this.imageKey = imageKey; }
    void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
    void setUpdatedBy(Integer updatedBy) { this.updatedBy = updatedBy; }
    public void setDeletedBy(Integer deletedBy) { this.deletedBy = deletedBy; }
}
