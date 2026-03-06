package com.tetris.tetrisburger_backend.domain.model;

public class MenuItem {

    private Integer idMenuItem;
    private Integer idMenu;
    private String itemType; // BURGER, PRODUCT
    private Integer idBurger;
    private Integer idProduct;
    private Integer quantity;

    public MenuItem() {}

    private MenuItem(Integer idMenuItem, Integer idMenu, String itemType,
                     Integer idBurger, Integer idProduct, Integer quantity) {
        this.idMenuItem = idMenuItem;
        this.idMenu = idMenu;
        this.itemType = itemType;
        this.idBurger = idBurger;
        this.idProduct = idProduct;
        this.quantity = quantity;
    }

    public static MenuItem create(String itemType, Integer idBurger,
                                  Integer idProduct, Integer quantity) {
        if (itemType == null || itemType.isBlank())
            throw new IllegalArgumentException("El tipo de ítem es obligatorio");
        if ("BURGER".equals(itemType) && idBurger == null)
            throw new IllegalArgumentException("idBurger es obligatorio para tipo BURGER");
        if ("PRODUCT".equals(itemType) && idProduct == null)
            throw new IllegalArgumentException("idProduct es obligatorio para tipo PRODUCT");
        if (quantity == null || quantity <= 0)
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");

        MenuItem item = new MenuItem();
        item.itemType = itemType;
        item.idBurger = idBurger;
        item.idProduct = idProduct;
        item.quantity = quantity;
        return item;
    }

    public static MenuItem reconstitute(Integer idMenuItem, Integer idMenu, String itemType,
                                        Integer idBurger, Integer idProduct, Integer quantity) {
        return new MenuItem(idMenuItem, idMenu, itemType, idBurger, idProduct, quantity);
    }

    // Getters y Setters
    public Integer getIdMenuItem() { return idMenuItem; }
    public void setIdMenuItem(Integer idMenuItem) { this.idMenuItem = idMenuItem; }

    public Integer getIdMenu() { return idMenu; }
    public void setIdMenu(Integer idMenu) { this.idMenu = idMenu; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public Integer getIdBurger() { return idBurger; }
    public void setIdBurger(Integer idBurger) { this.idBurger = idBurger; }

    public Integer getIdProduct() { return idProduct; }
    public void setIdProduct(Integer idProduct) { this.idProduct = idProduct; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}