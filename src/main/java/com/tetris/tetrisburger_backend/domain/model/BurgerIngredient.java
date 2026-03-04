package com.tetris.tetrisburger_backend.domain.model;

import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;

import java.math.BigDecimal;

public class BurgerIngredient {

    private Integer idBurgerIngredient;
    private Integer idProduct;
    private String productName;
    private BigDecimal priceAtTime;
    private int quantity;
    private boolean isOptional;
    private BigDecimal subtotal;
    private String imageUrl;

    private BurgerIngredient() {}

    // ==================== Reconstitución ====================

    public static BurgerIngredient reconstitute(
            Integer idBurgerIngredient,
            Integer idProduct,
            String productName,
            BigDecimal priceAtTime,
            Integer quantity,
            BigDecimal subtotal,
            Boolean isOptional,
            String imageUrl
    ) {
        BurgerIngredient ingredient = new BurgerIngredient();
        ingredient.idBurgerIngredient = idBurgerIngredient;
        ingredient.idProduct = idProduct;
        ingredient.productName = productName;
        ingredient.priceAtTime = priceAtTime;
        ingredient.quantity = quantity != null ? quantity : 0;
        ingredient.subtotal = subtotal != null ? subtotal : BigDecimal.ZERO;
        ingredient.isOptional = isOptional != null ? isOptional : false;
        ingredient.imageUrl = imageUrl;
        return ingredient;
    }

    // ==================== Desde Snapshot ====================

    public static BurgerIngredient fromSnapshot(ProductSnapshot snapshot) {
        if (snapshot == null)
            throw new IllegalArgumentException("ProductSnapshot no puede ser null");

        BurgerIngredient ingredient = new BurgerIngredient();
        ingredient.idProduct = snapshot.idProduct();
        ingredient.productName = snapshot.name();
        ingredient.priceAtTime = snapshot.price();
        ingredient.quantity = snapshot.quantity();
        ingredient.subtotal = snapshot.calculateSubtotal();
        ingredient.isOptional = snapshot.isOptional() != null ? snapshot.isOptional() : false;
        ingredient.imageUrl = snapshot.imageUrl();
        return ingredient;
    }

    // ==================== Comportamiento ====================

    public BigDecimal calculateSubtotal() {
        if (priceAtTime == null) return BigDecimal.ZERO;
        return priceAtTime.multiply(BigDecimal.valueOf(quantity));
    }

    // ==================== Getters ====================

    public Integer getIdBurgerIngredient() { return idBurgerIngredient; }
    public Integer getIdProduct()          { return idProduct; }
    public String getProductName()         { return productName; }
    public BigDecimal getPriceAtTime()     { return priceAtTime; }
    public int getQuantity()               { return quantity; }
    public boolean getIsOptional()         { return isOptional; }


    public BigDecimal getSubtotal() {
        return subtotal != null ? subtotal : calculateSubtotal();
    }

    // ==================== Setters (solo infra) ====================


    public boolean isOptional() {
        return isOptional;
    }

    public void setOptional(boolean optional) {
        isOptional = optional;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setIdBurgerIngredient(Integer idBurgerIngredient) { this.idBurgerIngredient = idBurgerIngredient; }
    public void setIdProduct(Integer idProduct)                   { this.idProduct = idProduct; }
    public void setProductName(String productName)                { this.productName = productName; }
    public void setPriceAtTime(BigDecimal priceAtTime)            { this.priceAtTime = priceAtTime; }
    public void setQuantity(int quantity)                         { this.quantity = quantity; }
    public void setSubtotal(BigDecimal subtotal)                  { this.subtotal = subtotal; }
    public void setIsOptional(boolean isOptional)                   { this.isOptional = isOptional; }
}
