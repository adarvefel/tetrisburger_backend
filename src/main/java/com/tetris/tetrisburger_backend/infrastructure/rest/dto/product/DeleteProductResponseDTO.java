package com.tetris.tetrisburger_backend.infrastructure.rest.dto.product;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteProductResponseDTO {

    private Integer id;
    private String name;
    private String description;
    private Integer quantity;
    private BigDecimal price;
    private Boolean availability;
    private String productType;
    private String ingredientType;
    private Boolean burgerIngredient;
    private String imageUrl;
    private String imageStatus;
    private Integer productCategoryId;
    private Integer supplierId;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private Integer createdBy;
    private Integer updatedBy;
    private Integer deletedBy;
}
