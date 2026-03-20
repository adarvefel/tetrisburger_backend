package com.tetris.tetrisburger_backend.domain.model;

import com.tetris.tetrisburger_backend.domain.exception.InvalidCartItemException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Cart {

    // ============================================
    // CAMPOS
    // ============================================

    private Integer idCart;
    private Integer idUser;
    private List<CartItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ============================================
    // CONSTRUCTOR PRIVADO
    // ============================================

    private Cart() {
        this.items = new ArrayList<>();
    }

    // ============================================
    // FACTORY: Reconstitución desde persistencia
    // ============================================

    public static Cart reconstitute(
            Integer idCart,
            Integer idUser,
            List<CartItem> items,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        Cart c = new Cart();
        c.idCart    = idCart;
        c.idUser    = idUser;
        c.items     = items != null ? new ArrayList<>(items) : new ArrayList<>();
        c.createdAt = createdAt;
        c.updatedAt = updatedAt;
        return c;
    }

    // ============================================
    // FACTORY: Nuevo carrito para usuario
    // ============================================

    public static Cart createForUser(Integer idUser) {
        if (idUser == null)
            throw new IllegalArgumentException("El idUser es obligatorio");

        Cart c = new Cart();
        c.idUser    = idUser;
        c.createdAt = LocalDateTime.now();
        return c;
    }

    // ============================================
    // COMPORTAMIENTO
    // ============================================

    /**
     * Reemplaza todos los ítems del carrito con los recibidos del localStorage.
     * El localStorage es la fuente de verdad — se hace replace completo.
     */
    public void sync(List<CartItem> newItems) {
        if (newItems == null)
            throw new IllegalArgumentException("Los ítems no pueden ser null");

        this.items = new ArrayList<>(newItems);
        this.updatedAt = LocalDateTime.now();
    }


    public void clear() {
        this.items = new ArrayList<>();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public void validateItems(List<CartItem> items) {
        if (items == null)
            throw new InvalidCartItemException("Los ítems no pueden ser null");

        for (CartItem item : items) {
            if (item.getItemType() == null)
                throw new InvalidCartItemException("El tipo de ítem es obligatorio");
            if (item.getIdItem() == null || item.getIdItem() <= 0)
                throw new InvalidCartItemException("El idItem debe ser mayor a 0");
            if (item.getName() == null || item.getName().isBlank())
                throw new InvalidCartItemException("El nombre del ítem es obligatorio");
            if (item.getUnitPrice() == null || item.getUnitPrice().signum() < 0)
                throw new InvalidCartItemException("El precio unitario no puede ser negativo");
            if (item.getQuantity() == null || item.getQuantity() <= 0)
                throw new InvalidCartItemException("La cantidad debe ser mayor a 0");
        }
    }

    // ============================================
    // GETTERS
    // ============================================

    public Integer getIdCart()          { return idCart; }
    public Integer getIdUser()          { return idUser; }
    public List<CartItem> getItems()    { return new ArrayList<>(items); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // ============================================
    // SETTERS (solo para infraestructura)
    // ============================================

    public void setIdCart(Integer idCart) { this.idCart = idCart; }
}