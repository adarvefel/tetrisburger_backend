package com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductCategoryResponseDTO {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
}