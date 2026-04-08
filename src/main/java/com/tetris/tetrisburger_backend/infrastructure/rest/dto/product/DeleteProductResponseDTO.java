package com.tetris.tetrisburger_backend.infrastructure.rest.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory.ProductCategoryResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO de producto eliminado con auditoría completa")
public class DeleteProductResponseDTO {

    @Schema(description = "ID del producto", example = "15")
    private Integer id;

    @Schema(description = "Nombre del producto", example = "Carne de Res Premium")
    private String name;

    @Schema(description = "Descripción del producto")
    private String description;

    @Schema(description = "Cantidad en stock", example = "50")
    private Integer quantity;

    @Schema(description = "Precio del producto", example = "8000.00")
    private BigDecimal price;

    @Schema(description = "Disponibilidad", example = "false")
    private Boolean availability;

    @Schema(description = "Tipo de producto", example = "INGREDIENT")
    private String productType;

    private Boolean isBurgerIngredient;

    @Schema(description = "Categoría del producto")
    private ProductCategoryResponseDTO productCategory;

    private Supplier supplier;

    @Schema(description = "URL de la imagen")
    private String imageUrl;

    @Schema(description = "Estado de la imagen", example = "DELETED")
    private String imageStatus;

    // ==================== AUDITORÍA ====================

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización")
    private LocalDateTime updatedAt;

    @Schema(description = "Fecha de eliminación")
    private LocalDateTime deletedAt;

    @Schema(description = "ID del usuario que creó el producto")
    private Integer createdBy;

    @Schema(description = "ID del usuario que actualizó el producto")
    private Integer updatedBy;

    @Schema(description = "ID del usuario que eliminó el producto")
    private Integer deletedBy;
}
