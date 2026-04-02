package com.tetris.tetrisburger_backend.domain.model;

import com.tetris.tetrisburger_backend.domain.enums.ItemType;

public class MenuItem {

    private Integer idMenuItem;
    private Menu menu;
    private ItemType itemType;
    private Burger burger;
    private Product product;
    private Integer quantity;

    public MenuItem() {}

    private MenuItem(Integer idMenuItem, Menu menu, ItemType itemType,
                     Burger burger, Product product, Integer quantity) {
        this.idMenuItem = idMenuItem;
        this.menu = menu;
        this.itemType = itemType;
        this.burger = burger;
        this.product = product;
        this.quantity = quantity;
    }

    // ── Nuevo ítem (menu se asigna luego con assignMenu)
    public static MenuItem create(ItemType itemType, Burger burger,
                                  Product product, Integer quantity) {
        validate(itemType, burger, product, quantity);

        MenuItem item = new MenuItem();
        item.itemType = itemType;
        item.burger = burger;
        item.product = product;
        item.quantity = quantity;
        return item;
    }

    // ── Rehidratación desde BD
    public static MenuItem reconstitute(Integer idMenuItem, Menu menu,
                                        ItemType itemType, Burger burger,
                                        Product product, Integer quantity) {
        return new MenuItem(idMenuItem, menu, itemType, burger, product, quantity);
    }

    // ── Actualizar ítem existente
    public void update(ItemType itemType, Burger burger,
                       Product product, Integer quantity) {
        validate(itemType, burger, product, quantity);
        this.itemType = itemType;
        this.burger = burger;
        this.product = product;
        this.quantity = quantity;
    }

    // ── Asignar menú padre tras persistir Menu
    public void assignMenu(Menu menu) {
        if (menu == null)
            throw new IllegalArgumentException("menu no puede ser nulo");
        this.menu = menu;
    }

    // ── Validación centralizada
    private static void validate(ItemType itemType, Burger burger,
                                 Product product, Integer quantity) {
        if (itemType == null)
            throw new IllegalArgumentException("El tipo de ítem es obligatorio");
        if (itemType == ItemType.BURGER && burger == null)
            throw new IllegalArgumentException("Se requiere una burger para tipo BURGER");
        if (itemType == ItemType.PRODUCT && product == null)
            throw new IllegalArgumentException("Se requiere un producto para tipo PRODUCT");
        if (quantity == null || quantity <= 0)
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
    }

    // ==================== GETTERS Y SETTERS ====================

    public Integer getIdMenuItem() { return idMenuItem; }
    public void setIdMenuItem(Integer idMenuItem) { this.idMenuItem = idMenuItem; }

    public Menu getMenu() { return menu; }
    public void setMenu(Menu menu) { this.menu = menu; }

    public ItemType getItemType() { return itemType; }
    public void setItemType(ItemType itemType) { this.itemType = itemType; }

    public Burger getBurger() { return burger; }
    public void setBurger(Burger burger) { this.burger = burger; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}