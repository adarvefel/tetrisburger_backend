package com.tetris.tetrisburger_backend.infrastructure.rest.dto.product;

import lombok.Data;

import java.util.List;

@Data
public class BurgerIngredientListDTO {

    private List<BurgerIngredientOptionDTO> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public BurgerIngredientListDTO(List<BurgerIngredientOptionDTO> items,
                                   int page, int size,
                                   long totalElements, int totalPages) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }


}