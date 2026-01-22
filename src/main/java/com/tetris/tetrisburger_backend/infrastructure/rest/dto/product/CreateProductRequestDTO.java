// src/main/java/com/tetris/tetrisburger_backend/infrastructure/rest/dto/product/CreateProductRequestDTO.java
package com.tetris.tetrisburger_backend.infrastructure.rest.dto.product;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductRequestDTO {
    @NotBlank
    private String name;
    @Size(max = 255)
    private String description;
    @NotNull
    @Min(0)
    private Integer quantity;
    @NotNull
    @Digits(integer = 11, fraction = 2)
    private BigDecimal price;
    @NotNull
    private Boolean availability;
    private String productType;
    private String ingredientType;
    @NotNull
    private Boolean burgerIngredient;
    @Size(max = 255)
    private String imageUrl; // nuevo
    @NotNull
    private Integer productCategoryId;
    @NotNull
    private Integer supplierId;
}
