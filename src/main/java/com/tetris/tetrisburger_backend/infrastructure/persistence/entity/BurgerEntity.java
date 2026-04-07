package com.tetris.tetrisburger_backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "burger")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BurgerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_burger")
    private Integer idBurger;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "final_price", precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @Column(name = "margin", precision = 10, scale = 2)
    private BigDecimal margin;

    @Column(name = "margin_percentage", precision = 10, scale = 2)
    private BigDecimal marginPercentage;


    private Boolean sellingAtLoss = false;

    @Column(name = "is_on_menu", nullable = false)
    private Boolean isOnMenu;

    @Column(name = "is_custom", nullable = false)  // ← renombrado en BD también
    private Boolean custom;


    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @Column(name = "is_available", nullable = false)
    private Boolean availability = true;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "image_key", length = 255)
    private String imageKey;

    @Column(name = "id_user")
    private Integer idUser;

    @Column(name = "times_ordered")
    private Integer timesOrdered = 0;

    @OneToMany(
            mappedBy = "burger",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<BurgerIngredientEntity> ingredients = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_by", updatable = false)
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "deleted_by")
    private Integer deletedBy;

    @PrePersist
    @PreUpdate
    public void syncMetrics() {
        if (basePrice == null) basePrice = BigDecimal.ZERO;
        if (finalPrice == null) finalPrice = BigDecimal.ZERO;

        this.margin = finalPrice.subtract(basePrice);
        this.marginPercentage = basePrice.compareTo(BigDecimal.ZERO) > 0
                ? margin.divide(basePrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        this.sellingAtLoss = finalPrice.compareTo(basePrice) < 0;
    }

    public void addIngredient(BurgerIngredientEntity ingredient) {
        if (ingredient == null) return;
        ingredient.setBurger(this);
        this.ingredients.add(ingredient);
    }

    public void removeIngredient(BurgerIngredientEntity ingredient) {
        if (ingredient == null) return;
        ingredient.setBurger(null);
        this.ingredients.remove(ingredient);
    }
}
