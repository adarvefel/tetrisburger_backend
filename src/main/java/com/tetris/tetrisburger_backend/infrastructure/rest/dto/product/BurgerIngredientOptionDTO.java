package com.tetris.tetrisburger_backend.infrastructure.rest.dto.product;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class BurgerIngredientOptionDTO {

    private Integer idProduct;
    private String name;
    private BigDecimal price;
    private String imageUrl;
    private Boolean availability;
    private Integer quantity;


}