package com.tetris.tetrisburger_backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL")
@EntityListeners(AuditingEntityListener.class)
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_product")
    private Integer id;

    private String name;
    private String description;
    private Integer quantity;

    @Column(precision = 13, scale = 2)
    private BigDecimal price;

    private Boolean availability;

    @Column(name = "product_type")
    private String productType;

    @Column(name = "ingredient_type")
    private String ingredientType;

    @Column(name = "is_burger_ingredient")
    private Boolean burgerIngredient;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "image_key")
    private String imageKey;

    @Column(name = "id_product_category")
    private Integer productCategoryId;

    @Column(name = "id_supplier")
    private Integer supplierId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private Integer createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "deleted_by")
    private Integer deletedBy;

    public Integer getId() {
        return id;
    }
}
