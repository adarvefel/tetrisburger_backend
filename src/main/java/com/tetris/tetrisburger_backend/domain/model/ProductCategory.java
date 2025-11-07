package com.tetris.tetrisburger_backend.domain.model;

public class ProductCategory {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;

    private ProductCategory(Integer id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public static ProductCategory ofNew(String name, String description, Boolean available) {
        return new ProductCategory(null, name, description, available != null ? available : Boolean.TRUE);
    }

    public static ProductCategory of(Integer id, String name, String description, Boolean available) {
        return new ProductCategory(id, name, description, available != null ? available : Boolean.TRUE);
    }

    public void update(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available != null ? available : Boolean.TRUE;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}