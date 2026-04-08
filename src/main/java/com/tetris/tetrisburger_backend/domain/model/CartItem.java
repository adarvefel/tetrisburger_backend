package com.tetris.tetrisburger_backend.domain.model;

import com.tetris.tetrisburger_backend.domain.exception.InvalidCartItemException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CartItem {

    // ============================================
    // CAMPOS
    // ============================================

    private Integer idCartItem;
    private Integer idCart;
    private ItemType itemType;
    private Integer idItem;        // id de burger, product o addition según itemType
    private String name;           // snapshot al momento de agregar
    private String imageUrl;       // snapshot al momento de agregar
    private BigDecimal unitPrice;  // snapshot — no se recalcula desde catálogo
    private Integer quantity;
    private BigDecimal subtotal;   // unitPrice * quantity
    private LocalDateTime createdAt;

    // ============================================
    // ENUM
    // ============================================

    public enum ItemType {
        BURGER, PRODUCT, ADDITION
    }

    // ============================================
    // CONSTRUCTOR PRIVADO
    // ============================================

    private CartItem() {}

    // ============================================
    // FACTORY: Reconstitución desde persistencia
    // ============================================

    public static CartItem reconstitute(
            Integer idCartItem,
            Integer idCart,
            ItemType itemType,
            Integer idItem,
            String name,
            String imageUrl,
            BigDecimal unitPrice,
            Integer quantity,
            BigDecimal subtotal,
            LocalDateTime createdAt
    ) {
        CartItem ci = new CartItem();
        ci.idCartItem = idCartItem;
        ci.idCart     = idCart;
        ci.itemType   = itemType;
        ci.idItem     = idItem;
        ci.name       = name;
        ci.imageUrl   = imageUrl;
        ci.unitPrice  = unitPrice;
        ci.quantity   = quantity;
        ci.subtotal   = subtotal;
        ci.createdAt  = createdAt;
        return ci;
    }

    // ============================================
    // FACTORY: Desde payload del localStorage
    // ============================================

    public static CartItem fromSync(
            ItemType itemType,
            Integer idItem,
            String name,
            String imageUrl,
            BigDecimal unitPrice,
            Integer quantity
    ) {
        if (itemType == null)
            throw new InvalidCartItemException("El tipo de ítem es obligatorio");
        if (idItem == null || idItem <= 0)
            throw new InvalidCartItemException("El idItem debe ser mayor a 0");
        if (name == null || name.isBlank())
            throw new InvalidCartItemException("El nombre es obligatorio");
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0)
            throw new InvalidCartItemException("El precio unitario no puede ser negativo");
        if (quantity == null || quantity <= 0)
            throw new InvalidCartItemException("La cantidad debe ser mayor a 0");

        CartItem ci = new CartItem();
        ci.itemType  = itemType;
        ci.idItem    = idItem;
        ci.name      = name;
        ci.imageUrl  = imageUrl;
        ci.unitPrice = unitPrice;
        ci.quantity  = quantity;
        ci.subtotal  = unitPrice.multiply(BigDecimal.valueOf(quantity));
        ci.createdAt = LocalDateTime.now();
        return ci;
    }

    // ============================================
    // QUERIES
    // ============================================

    public boolean isBurger()   { return ItemType.BURGER.equals(this.itemType); }
    public boolean isProduct()  { return ItemType.PRODUCT.equals(this.itemType); }
    public boolean isAdicion()  { return ItemType.ADDITION.equals(this.itemType); }

    // ============================================
    // GETTERS
    // ============================================

    public Integer getIdCartItem()    { return idCartItem; }
    public Integer getIdCart()        { return idCart; }
    public ItemType getItemType()     { return itemType; }
    public Integer getIdItem()        { return idItem; }
    public String getName()           { return name; }
    public String getImageUrl()       { return imageUrl; }
    public BigDecimal getUnitPrice()  { return unitPrice; }
    public Integer getQuantity()      { return quantity; }
    public BigDecimal getSubtotal()   { return subtotal; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ============================================
    // SETTERS (solo para infraestructura)
    // ============================================

    public void setIdCartItem(Integer idCartItem) { this.idCartItem = idCartItem; }
    public void setIdCart(Integer idCart)         { this.idCart = idCart; }
}