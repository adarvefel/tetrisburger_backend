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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_burger", nullable = false)
    private BurgerEntity burger;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_product", nullable = false, insertable = false, updatable = false)
    private ProductEntity product;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "id_product", nullable = false)
    private Integer idProduct;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;


    @Column(name = "price_at_time", nullable = false)
    private BigDecimal priceAtTime;

    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal;

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

    public Integer getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(Integer idProduct) {
        this.idProduct = idProduct;
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
