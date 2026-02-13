// src/main/java/com/tetris/tetrisburger_backend/infrastructure/persistence/entity/BurgerEntity.java
package com.tetris.tetrisburger_backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
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

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "base_price")
    private BigDecimal basePrice;

    @Column(name = "final_price")
    private BigDecimal finalPrice;

    @Column(name = "is_on_menu", nullable = false)
    private Boolean isOnMenu;

    @Column(name = "is_favorite", nullable = false)
    private Boolean isFavorite;

    @Column(name = "is_custom", nullable = false)
    private Boolean isCustom;

    @Column(name = "availability", nullable = false)
    private Boolean availability;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "image_key")
    private String imageKey;

    @Column(name = "id_user")
    private Integer idUser;

    @Column(name = "times_ordered")
    private Integer timesOrdered;

    // Auditoría de fechas
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Soft delete: puede ser NULL mientras la burger esté activa
    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    // Auditoría de usuario (pueden ser NULL si aún no tienes auditor configurado)
    @Column(name = "created_by", updatable = false)
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "deleted_by")
    private Integer deletedBy;

    @OneToMany(
            mappedBy = "burger",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<BurgerIngredientEntity> ingredients = new ArrayList<>();


    // ========= HELPERS =========

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
