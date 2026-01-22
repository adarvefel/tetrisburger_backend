package com.tetris.tetrisburger_backend.infrastructure.rest.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListProductResponseDTO {

    private List<ProductResponseDTO> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}