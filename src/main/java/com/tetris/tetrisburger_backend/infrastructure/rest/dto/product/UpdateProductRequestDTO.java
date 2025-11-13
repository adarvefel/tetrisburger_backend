// src/main/java/com/tetris/tetrisburger_backend/infrastructure/rest/dto/product/UpdateProductRequestDTO.java
package com.tetris.tetrisburger_backend.infrastructure.rest.dto.product;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductRequestDTO {
    private String name;
    private String description;
    private Integer quantity;
    private BigDecimal price;
    private Boolean availability;
    private String productType;
    private String ingredientType;
    private Boolean burgerIngredient;
    @Size(max = 255)
    private String imageUrl; // nuevo
    private Integer productCategoryId;
    private Integer supplierId;
}
