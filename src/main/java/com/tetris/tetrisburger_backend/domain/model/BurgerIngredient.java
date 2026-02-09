package com.tetris.tetrisburger_backend.domain.model;

import com.tetris.tetrisburger_backend.domain.port.in.burger.command.ProductSnapshot;

import java.math.BigDecimal;

public class BurgerIngredient {

    private Integer idBurgerIngredient;
    private Integer idProduct;
    private String productName;
    private BigDecimal priceAtTime;
    private int quantity;
    private BigDecimal subtotal;
    private boolean isOptional;

    // Constructor privado
    private BurgerIngredient() {}

    // ==================== Factory Method: Reconstitución ====================

    /**
     *  Reconstitución completa desde persistencia
     */
    public static BurgerIngredient reconstitute(
            Integer idBurgerIngredient,
            Integer idProduct,
            String productName,
            BigDecimal priceAtTime,
            Integer quantity,
            BigDecimal subtotal,
            Boolean isOptional
    ) {
        BurgerIngredient ingredient = new BurgerIngredient();
        ingredient.idBurgerIngredient = idBurgerIngredient;
        ingredient.idProduct = idProduct;
        ingredient.productName = productName;
        ingredient.priceAtTime = priceAtTime;
        ingredient.quantity = quantity;
        ingredient.subtotal = subtotal != null ? subtotal : BigDecimal.ZERO;
        ingredient.isOptional = isOptional != null ? isOptional : false;
        return ingredient;
    }

    // ==================== Factory Method: Desde Snapshot ====================

    /**
     * Crea un ingrediente desde un ProductSnapshot (al crear burger)
     */
    public static BurgerIngredient fromSnapshot(ProductSnapshot snapshot) {
        if (snapshot == null) {
            throw new IllegalArgumentException("ProductSnapshot no puede ser null");
        }

        BurgerIngredient ingredient = new BurgerIngredient();
        ingredient.idProduct = snapshot.idProduct();
        ingredient.productName = snapshot.name();
        ingredient.priceAtTime = snapshot.price();
        ingredient.quantity = snapshot.quantity();
        ingredient.subtotal = snapshot.price().multiply(new BigDecimal(snapshot.quantity()));
        ingredient.isOptional = snapshot.isOptional();
        return ingredient;
    }

    // ==================== Comportamiento ====================

    /**
     * Calcula el subtotal (precio × cantidad)
     */
    public BigDecimal calculateSubtotal() {
        if (priceAtTime == null) {
            return BigDecimal.ZERO;
        }
        return priceAtTime.multiply(BigDecimal.valueOf(quantity));
    }


    // ==================== Getters ====================

    public Integer getIdBurgerIngredient() {
        return idBurgerIngredient;
    }

    public Integer getIdProduct() {
        return idProduct;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getPriceAtTime() {
        return priceAtTime;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        if (subtotal == null) {
            return calculateSubtotal();
        }
        return subtotal;
    }

    public boolean isOptional() {
        return isOptional;
    }

    // ==================== Setters (solo para infraestructura) ====================

    public void setIdBurgerIngredient(Integer idBurgerIngredient) {
        this.idBurgerIngredient = idBurgerIngredient;
    }

    public void setIdProduct(Integer idProduct) {
        this.idProduct = idProduct;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setPriceAtTime(BigDecimal priceAtTime) {
        this.priceAtTime = priceAtTime;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public void setOptional(boolean optional) {
        this.isOptional = optional;
    }

    // Para compatibilidad con mappers
    public Boolean getIsOptional() {
        return isOptional;
    }
}
