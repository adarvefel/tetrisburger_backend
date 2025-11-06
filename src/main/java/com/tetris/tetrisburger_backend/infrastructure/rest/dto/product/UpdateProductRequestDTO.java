package com.tetris.tetrisburger_backend.infrastructure.rest.dto.product;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequestDTO {
    @Size(max = 255)
    private String description;

    @Min(0)
    private Integer quantity;

    @Digits(integer = 11, fraction = 2)
    private BigDecimal price;

    private String name;
    private Boolean availability;
    private String productType;
    private String ingredientType;
    private Boolean burgerIngredient;
    private Integer productCategoryId;
    private Integer supplierId;


}