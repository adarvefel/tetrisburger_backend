package com.tetris.tetrisburger_backend.domain.model;

import java.math.BigDecimal;

public class OrderItemIngredient {

    private Integer idOrderItemIngredient;
    private Integer idOrderItem;
    private Integer idProduct;
    private String productName;
    private Integer quantity;
    private BigDecimal price;

    private OrderItemIngredient() {}

    public static OrderItemIngredient create(
            Integer idProduct, String productName,
            Integer quantity, BigDecimal price
    ) {
        OrderItemIngredient i = new OrderItemIngredient();
        i.idProduct = idProduct;
        i.productName = productName;
        i.quantity = quantity;
        i.price = price;
        return i;
    }

    public static OrderItemIngredient reconstitute(
            Integer idOrderItemIngredient, Integer idOrderItem,
            Integer idProduct, String productName,
            Integer quantity, BigDecimal price
    ) {
        OrderItemIngredient i = new OrderItemIngredient();
        i.idOrderItemIngredient = idOrderItemIngredient;
        i.idOrderItem = idOrderItem;
        i.idProduct = idProduct;
        i.productName = productName;
        i.quantity = quantity;
        i.price = price;
        return i;
    }

    public Integer getIdOrderItemIngredient() { return idOrderItemIngredient; }
    public Integer getIdOrderItem()           { return idOrderItem; }
    public Integer getIdProduct()             { return idProduct; }
    public String getProductName()            { return productName; }
    public Integer getQuantity()              { return quantity; }
    public BigDecimal getPrice()              { return price; }
    public void setIdOrderItemIngredient(Integer id) { this.idOrderItemIngredient = id; }
    public void setIdOrderItem(Integer id)    { this.idOrderItem = id; }
}