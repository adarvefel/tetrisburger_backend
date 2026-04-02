package com.tetris.tetrisburger_backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_category",
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_product_category_name", columnNames = "product_category_name")
        },
        indexes = {
                @Index(name = "idx_product_category_name", columnList = "product_category_name")
        })
public class ProductCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_product_category")
    private Integer id;

    @Column(name = "product_category_name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "available", nullable = false)
    private Boolean available;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_by", nullable = false, updatable = false)
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "deleted_by")
    private Integer deletedBy;

    // ── Getters y Setters ────────────────────────────────────────────────────
    public Integer getId()                  { return id; }
    public void setId(Integer id)           { this.id = id; }

    public String getName()                 { return name; }
    public void setName(String name)        { this.name = name; }

    public String getDescription()          { return description; }
    public void setDescription(String d)    { this.description = d; }

    public Boolean getAvailable()           { return available; }
    public void setAvailable(Boolean a)     { this.available = a; }

    public LocalDateTime getCreatedAt()     { return createdAt; }
    public void setCreatedAt(LocalDateTime v){ this.createdAt = v; }

    public LocalDateTime getUpdatedAt()     { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v){ this.updatedAt = v; }

    public LocalDateTime getDeletedAt()     { return deletedAt; }
    public void setDeletedAt(LocalDateTime v){ this.deletedAt = v; }

    public Integer getCreatedBy()           { return createdBy; }
    public void setCreatedBy(Integer v)     { this.createdBy = v; }

    public Integer getUpdatedBy()           { return updatedBy; }
    public void setUpdatedBy(Integer v)     { this.updatedBy = v; }

    public Integer getDeletedBy()           { return deletedBy; }
    public void setDeletedBy(Integer v)     { this.deletedBy = v; }
}