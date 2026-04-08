package com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListProductCategoryResponseDTO {
    private List<ProductCategoryResponseDTO> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}