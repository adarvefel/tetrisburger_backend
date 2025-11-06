package com.tetris.tetrisburger_backend.infrastructure.rest.dto.product;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private Integer productCategoryId;

    @NotNull
    private Integer supplierId;


}