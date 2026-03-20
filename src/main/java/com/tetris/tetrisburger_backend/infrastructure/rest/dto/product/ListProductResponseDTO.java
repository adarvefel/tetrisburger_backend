package com.tetris.tetrisburger_backend.infrastructure.rest.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de lista paginada de productos")
public class ListProductResponseDTO {

    @Schema(description = "Lista de productos")
    private List<ProductResponseDTO> items;

    @Schema(description = "Número de página actual", example = "0")
    private int page;

    @Schema(description = "Tamaño de página", example = "10")
    private int size;

    @Schema(description = "Total de elementos", example = "45")
    private long totalElements;

    @Schema(description = "Total de páginas", example = "5")
    private int totalPages;
}
