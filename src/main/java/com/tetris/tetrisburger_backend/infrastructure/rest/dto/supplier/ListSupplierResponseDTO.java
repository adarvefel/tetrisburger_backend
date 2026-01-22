package com.tetris.tetrisburger_backend.infrastructure.rest.dto.supplier;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListSupplierResponseDTO {
    private List<SupplierResponseDTO> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}