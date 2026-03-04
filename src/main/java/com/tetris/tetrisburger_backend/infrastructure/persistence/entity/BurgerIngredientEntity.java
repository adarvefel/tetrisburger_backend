package com.tetris.tetrisburger_backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Entity
@Table(name = "burger_ingredient")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BurgerIngredientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_burger_ingredient")
    private Integer idBurgerIngredient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_burger", nullable = false)
    private BurgerEntity burger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_product", nullable = false)
    private ProductEntity product;

    @Column(name = "product_name", nullable = false)
    private String productName;


    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "is_optional", nullable = false)
    private Boolean isOptional;


    @Column(name = "price_at_time", nullable = false)
    private BigDecimal priceAtTime;

    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal;


    @Column(name = "image_url", length = 500)
    private String imageUrl;


    // ============================================
    // GETTERS Y SETTERS
    // ============================================

    public Integer getIdBurgerIngredient() {
        return idBurgerIngredient;
    }

    public void setIdBurgerIngredient(Integer idBurgerIngredient) {
        this.idBurgerIngredient = idBurgerIngredient;
    }

    public BurgerEntity getBurger() {
        return burger;
    }

    public void setBurger(BurgerEntity burger) {
        this.burger = burger;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    public Boolean getOptional() {
        return isOptional;
    }

    public void setOptional(Boolean optional) {
        isOptional = optional;
    }

    public String getProductName() {
        return productName;
    }


    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getIsOptional() {
        return isOptional;
    }


    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public void setIsOptional(Boolean isOptional) {
        this.isOptional = isOptional;
    }

    public BigDecimal getPriceAtTime() {
        return priceAtTime;
    }

    public void setPriceAtTime(BigDecimal priceAtTime) {
        this.priceAtTime = priceAtTime;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
