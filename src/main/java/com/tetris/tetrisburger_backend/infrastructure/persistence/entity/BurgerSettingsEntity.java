package com.tetris.tetrisburger_backend.infrastructure.persistence.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "burger_settings")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BurgerSettingsEntity {
    @Id
    @Column(name="id_settings")
    private Integer idSettings;

    @Column(name = "custom_burger_min_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal customBurgerMinPrice;

    @Column(name = "custom_burger_max_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal customBurgerMaxPrice;


    @Column(name = "min_ingredients", nullable = false)
    private Integer minIngredients;

    @Column(name = "max_ingredients", nullable = false)
    private Integer maxIngredients;

    @Column(name = "custom_burgers_enabled", nullable = false)
    private Boolean customBurgersEnabled;


    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


}
