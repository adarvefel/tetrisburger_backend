// src/main/java/com/tetris/tetrisburger_backend/infrastructure/rest/dto/product/ProductResponseDTO.java
package com.tetris.tetrisburger_backend.infrastructure.rest.dto.product;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
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
    private String imageUrl; // nuevo
    private Integer productCategoryId;
    private Integer supplierId;
}
