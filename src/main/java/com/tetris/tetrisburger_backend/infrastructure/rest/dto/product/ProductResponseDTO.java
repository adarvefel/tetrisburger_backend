package com.tetris.tetrisburger_backend.infrastructure.rest.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tetris.tetrisburger_backend.domain.model.ProductType;
import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory.ProductCategoryResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO de respuesta de producto")
public class ProductResponseDTO {

    @Schema(description = "ID del producto", example = "15")
    private Integer idProduct;

    @Schema(description = "Nombre del producto", example = "Carne de Res Premium")
    private String name;

    @Schema(description = "Descripción del producto", example = "Carne 100% angus")
    private String description;

    @Schema(description = "Cantidad en stock", example = "50")
    private Integer quantity;

    @Schema(description = "Precio unitario", example = "8000.00")
    private BigDecimal price;

    @Schema(description = "Disponibilidad", example = "true")
    private Boolean availability;

    @Schema(description = "Tipo de producto", example = "INGREDIENT")
    private ProductType productType;

    private Boolean isBurgerIngredient;


    @Schema(description = "Categoría del producto")
    private ProductCategoryResponseDTO productCategory;

    @Schema(description = "ID del proveedor", example = "1")
    private Supplier supplier;

    @Schema(description = "URL de la imagen", example = "https://tetrisburger-images.s3.us-east-1.amazonaws.com/products/123-carne.jpg")
    private String imageUrl;

    @Schema(description = "Estado de la imagen", example = "READY", allowableValues = {"NONE", "PENDING", "READY"})
    private String imageStatus;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización")
    private LocalDateTime updatedAt;

    @Schema(description = "ID del usuario que creó el producto")
    private Integer createdBy;

    @Schema(description = "ID del usuario que actualizó el producto")
    private Integer updatedBy;
}
