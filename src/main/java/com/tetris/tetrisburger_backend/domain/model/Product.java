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
    private String imageUrl;      // Nombre original: "hamburguesa_clasica.jpg"
    private String imageKey;      // Key S3: "products/123-uuid-hamburguesa_clasica.jpg"
    private Integer productCategoryId;
    private Integer supplierId;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private Integer createdBy;
    private Integer updatedBy;
    private Integer deletedBy;

    // ==================== CONSTRUCTORES ====================

    private Product() {}

    private Product(Integer id, String name, String description, Integer quantity, BigDecimal price,
                    Boolean availability, String productType, String ingredientType, Boolean burgerIngredient,
                    String imageUrl, String imageKey, Integer productCategoryId, Integer supplierId,
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
        this.imageKey = imageKey;
        this.productCategoryId = productCategoryId;
        this.supplierId = supplierId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.deletedBy = deletedBy;
    }

    // ==================== FACTORY METHODS ====================

    /**
     * Factory method para crear nuevo producto (usado por repositorio/mappers)
     */
    public static Product ofNew(String name, String description, Integer quantity, BigDecimal price,
                                Boolean availability, String productType, String ingredientType, Boolean burgerIngredient,
                                String imageUrl, String imageKey, Integer productCategoryId, Integer supplierId,
                                Integer createdBy) {
        return new Product(null, name, description, quantity, price, availability, productType, ingredientType,
                burgerIngredient, imageUrl, imageKey, productCategoryId, supplierId,
                null, null, null, createdBy, null, null);
    }

    /**
     * Factory method para reconstruir desde BD
     */
    public static Product of(Integer id, String name, String description, Integer quantity, BigDecimal price,
                             Boolean availability, String productType, String ingredientType, Boolean burgerIngredient,
                             String imageUrl, String imageKey, Integer productCategoryId, Integer supplierId,
                             Instant createdAt, Instant updatedAt, Instant deletedAt,
                             Integer createdBy, Integer updatedBy, Integer deletedBy) {
        return new Product(id, name, description, quantity, price, availability, productType, ingredientType,
                burgerIngredient, imageUrl, imageKey, productCategoryId, supplierId,
                createdAt, updatedAt, deletedAt, createdBy, updatedBy, deletedBy);
    }

    /**
     * Factory method para crear con validaciones (usado por use cases)
     */
    public static Product create(
            String name,
            String description,
            Integer quantity,
            BigDecimal price,
            Boolean availability,
            String productType,
            String ingredientType,
            Boolean burgerIngredient,
            String imageUrl,
            String imageKey,
            Integer productCategoryId,
            Integer supplierId,
            Integer createdBy
    ) {
        validateName(name);
        validateQuantity(quantity);
        validatePrice(price);
        validateProductType(productType);
        validateCategoryId(productCategoryId);
        validateSupplierId(supplierId);

        Product product = new Product();
        product.name = name;
        product.description = description;
        product.quantity = quantity;
        product.price = price;
        product.availability = availability != null ? availability : true;
        product.productType = productType;
        product.ingredientType = ingredientType;
        product.burgerIngredient = burgerIngredient != null ? burgerIngredient : false;
        product.imageUrl = imageUrl;
        product.imageKey = imageKey;
        product.productCategoryId = productCategoryId;
        product.supplierId = supplierId;
        product.createdAt = Instant.now();
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

    private static void validateProductType(String productType) {
        if (productType == null || productType.isBlank()) {
            throw new IllegalArgumentException("El tipo de producto es requerido");
        }
    }

    private static void validateCategoryId(Integer categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("La categoría es requerida");
        }
    }

    private static void validateSupplierId(Integer supplierId) {
        if (supplierId == null) {
            throw new IllegalArgumentException("El proveedor es requerido");
        }
    }

    // ==================== MÉTODOS DE ACTUALIZACIÓN (COMANDOS) ====================

    /**
     * Actualiza los datos principales del producto
     */
    public void updateDetails(
            String name,
            String description,
            Integer quantity,
            BigDecimal price,
            Boolean availability,
            String productType,
            String ingredientType,
            Boolean burgerIngredient,
            Integer productCategoryId,
            Integer supplierId,
            Integer updatedBy
    ) {
        validateName(name);
        validateQuantity(quantity);
        validatePrice(price);
        validateProductType(productType);
        validateCategoryId(productCategoryId);
        validateSupplierId(supplierId);

        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.availability = availability != null ? availability : this.availability;
        this.productType = productType;
        this.ingredientType = ingredientType;
        this.burgerIngredient = burgerIngredient != null ? burgerIngredient : this.burgerIngredient;
        this.productCategoryId = productCategoryId;
        this.supplierId = supplierId;
        this.updatedAt = Instant.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Actualiza la imagen del producto (key + nombre original)
     */
    public void updateImage(String imageKey, String imageUrl, Integer updatedBy) {
        this.imageKey = imageKey;
        this.imageUrl = imageUrl;
        this.updatedAt = Instant.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Actualiza solo el nombre
     */
    public void updateName(String name, Integer updatedBy) {
        validateName(name);
        this.name = name;
        this.updatedAt = Instant.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Actualiza solo la descripción
     */
    public void updateDescription(String description, Integer updatedBy) {
        this.description = description;
        this.updatedAt = Instant.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Actualiza el precio
     */
    public void updatePrice(BigDecimal price, Integer updatedBy) {
        validatePrice(price);
        this.price = price;
        this.updatedAt = Instant.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Actualiza el stock/cantidad estableciendo un valor específico
     */
    public void updateQuantity(Integer newQuantity, Integer updatedBy) {
        validateQuantity(newQuantity);
        this.quantity = newQuantity;
        this.updatedAt = Instant.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Ajusta el stock (suma o resta)
     * @param delta cantidad a ajustar (puede ser negativo)
     */
    public void adjustStock(int delta, Integer updatedBy) {
        int newQuantity = this.quantity + delta;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Stock insuficiente. Stock actual: " + this.quantity);
        }
        this.quantity = newQuantity;
        this.updatedAt = Instant.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Reduce el stock (para ventas)
     */
    public void reduceStock(int amount, Integer updatedBy) {
        if (amount <= 0) {
            throw new IllegalArgumentException("La cantidad a reducir debe ser positiva");
        }
        if (this.quantity < amount) {
            throw new IllegalArgumentException(
                    String.format("Stock insuficiente. Disponible: %d, Solicitado: %d", this.quantity, amount)
            );
        }
        this.quantity -= amount;
        this.updatedAt = Instant.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Aumenta el stock (para reabastecimiento)
     */
    public void increaseStock(int amount, Integer updatedBy) {
        if (amount <= 0) {
            throw new IllegalArgumentException("La cantidad a aumentar debe ser positiva");
        }
        this.quantity += amount;
        this.updatedAt = Instant.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Actualiza la disponibilidad del producto
     */
    public void updateAvailability(Boolean availability, Integer updatedBy) {
        this.availability = availability;
        this.updatedAt = Instant.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Habilita el producto
     */
    public void enable(Integer updatedBy) {
        this.availability = true;
        this.updatedAt = Instant.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Deshabilita el producto
     */
    public void disable(Integer updatedBy) {
        this.availability = false;
        this.updatedAt = Instant.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Actualiza la categoría
     */
    public void updateCategory(Integer productCategoryId, Integer updatedBy) {
        validateCategoryId(productCategoryId);
        this.productCategoryId = productCategoryId;
        this.updatedAt = Instant.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Actualiza el proveedor
     */
    public void updateSupplier(Integer supplierId, Integer updatedBy) {
        validateSupplierId(supplierId);
        this.supplierId = supplierId;
        this.updatedAt = Instant.now();
        this.updatedBy = updatedBy;
    }

    /**
     * Marca el producto como eliminado (soft delete)
     */
    public void markAsDeleted(Integer deletedBy) {
        this.deletedAt = Instant.now();
        this.deletedBy = deletedBy;
    }

    /**
     * Restaura un producto eliminado
     */
    public void restore(Integer updatedBy) {
        this.deletedAt = null;
        this.deletedBy = null;
        this.updatedAt = Instant.now();
        this.updatedBy = updatedBy;
    }

    // ==================== MÉTODOS DE CONSULTA (QUERIES) ====================

    /**
     * Verifica si el producto está disponible
     */
    public boolean isAvailable() {
        return Boolean.TRUE.equals(this.availability) && this.deletedAt == null;
    }

    /**
     * Verifica si el producto tiene stock
     */
    public boolean hasStock() {
        return this.quantity != null && this.quantity > 0;
    }

    /**
     * Verifica si el producto puede venderse
     */
    public boolean canBeSold() {
        return isAvailable() && hasStock();
    }

    /**
     * Verifica si el producto está eliminado
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    /**
     * Verifica si el producto tiene imagen
     */
    public boolean hasImage() {
        return this.imageKey != null && !this.imageKey.isEmpty();
    }

    // ==================== GETTERS ====================

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
    public Boolean getAvailability() { return availability; }
    public String getProductType() { return productType; }
    public String getIngredientType() { return ingredientType; }
    public Boolean getBurgerIngredient() { return burgerIngredient; }
    public String getImageUrl() { return imageUrl; }
    public String getImageKey() { return imageKey; }
    public Integer getProductCategoryId() { return productCategoryId; }
    public Integer getSupplierId() { return supplierId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getDeletedAt() { return deletedAt; }
    public Integer getCreatedBy() { return createdBy; }
    public Integer getUpdatedBy() { return updatedBy; }
    public Integer getDeletedBy() { return deletedBy; }

    // ==================== SETTERS (para JPA/MyBatis) ====================

    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setAvailability(Boolean availability) { this.availability = availability; }
    public void setProductType(String productType) { this.productType = productType; }
    public void setIngredientType(String ingredientType) { this.ingredientType = ingredientType; }
    public void setBurgerIngredient(Boolean burgerIngredient) { this.burgerIngredient = burgerIngredient; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setImageKey(String imageKey) { this.imageKey = imageKey; }
    public void setProductCategoryId(Integer productCategoryId) { this.productCategoryId = productCategoryId; }
    public void setSupplierId(Integer supplierId) { this.supplierId = supplierId; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
    public void setUpdatedBy(Integer updatedBy) { this.updatedBy = updatedBy; }
    public void setDeletedBy(Integer deletedBy) { this.deletedBy = deletedBy; }
}
