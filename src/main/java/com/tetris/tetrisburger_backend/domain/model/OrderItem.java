package com.tetris.tetrisburger_backend.domain.model;

import com.tetris.tetrisburger_backend.domain.enums.OrderItemType;

import java.math.BigDecimal;

public class OrderItem {

    private Integer idOrderItem;
    private Integer idOrder;
    private OrderItemType itemType;
    private Integer idBurger;
    private Integer idProduct;
    private String itemName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    private OrderItem() {}

    public static OrderItem create(
            OrderItemType itemType,
            Integer idBurger,
            Integer idProduct,
            String itemName,
            Integer quantity,
            BigDecimal unitPrice
    ) {
        OrderItem i = new OrderItem();
        i.itemType = itemType;
        i.idBurger = idBurger;
        i.idProduct = idProduct;
        i.itemName = itemName;
        i.quantity = quantity;
        i.unitPrice = unitPrice;
        i.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return i;
    }

    public static OrderItem reconstitute(
            Integer idOrderItem, Integer idOrder,
            OrderItemType itemType,
            Integer idBurger, Integer idProduct,
            String itemName, Integer quantity,
            BigDecimal unitPrice, BigDecimal subtotal
    ) {
        OrderItem i = new OrderItem();
        i.idOrderItem = idOrderItem;
        i.idOrder = idOrder;
        i.itemType = itemType;
        i.idBurger = idBurger;
        i.idProduct = idProduct;
        i.itemName = itemName;
        i.quantity = quantity;
        i.unitPrice = unitPrice;
        i.subtotal = subtotal;
        return i;
    }

    public Integer getIdOrderItem()    { return idOrderItem; }
    public Integer getIdOrder()        { return idOrder; }
    public OrderItemType getItemType() { return itemType; }
    public Integer getIdBurger()       { return idBurger; }
    public Integer getIdProduct()      { return idProduct; }
    public String getItemName()        { return itemName; }
    public Integer getQuantity()       { return quantity; }
    public BigDecimal getUnitPrice()   { return unitPrice; }
    public BigDecimal getSubtotal()    { return subtotal; }
    public void setIdOrderItem(Integer id) { this.idOrderItem = id; }
    public void setIdOrder(Integer id)     { this.idOrder = id; }
}