// src/main/java/com/tetris/tetrisburger_backend/infrastructure/persistence/entity/BurgerIngredientEntity.java
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

    @Column(name = "id_product", nullable = false)
    private Integer idProduct;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "is_optional", nullable = false)
    private Boolean isOptional;

    @Column(name = "price_at_time", nullable = false)
    private BigDecimal priceAtTime;

    // Getters y setters

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

    public Integer getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(Integer idProduct) {
        this.idProduct = idProduct;
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

    public void setIsOptional(Boolean optional) {
        isOptional = optional;
    }

    public BigDecimal getPriceAtTime() {
        return priceAtTime;
    }

    public void setPriceAtTime(BigDecimal priceAtTime) {
        this.priceAtTime = priceAtTime;
    }
}
