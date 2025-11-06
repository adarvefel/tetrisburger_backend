package com.tetris.tetrisburger_backend.infrastructure.rest.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {

    private Integer id;
    private String name;
    private String description;
    private Integer quantity;
    private BigDecimal price;
    private Boolean availability;
    private String productType;
    private String ingredientType;
    private Boolean burgerIngredient;
    private Integer productCategoryId;
    private Integer supplierId;
}