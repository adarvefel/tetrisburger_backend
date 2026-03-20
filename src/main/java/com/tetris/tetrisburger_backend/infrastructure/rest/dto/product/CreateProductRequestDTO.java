// src/main/java/com/tetris/tetrisburger_backend/infrastructure/rest/dto/product/CreateProductRequestDTO.java
package com.tetris.tetrisburger_backend.infrastructure.rest.dto.product;

import com.tetris.tetrisburger_backend.domain.model.ProductType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "DTO para crear un producto")
public class CreateProductRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    @Schema(description = "Nombre del producto", example = "Carne de Res Premium")
    private String name;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    @Schema(description = "Descripción del producto", example = "Carne 100% angus, jugosa y tierna")
    private String description;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad debe ser mayor o igual a 0")
    @Schema(description = "Cantidad en stock", example = "50")
    private Integer quantity;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "Formato de precio inválido")
    @Schema(description = "Precio unitario", example = "8000.00")
    private BigDecimal price;

    @Schema(description = "Disponibilidad del producto", example = "true")
    private Boolean availability;

    @NotNull(message = "El tipo de producto es obligatorio")
    @Schema(
            description = "Tipo de producto",
            example = "INGREDIENT",
            allowableValues = {"INGREDIENT", "FINISHED_PRODUCT", "BEVERAGE", "SIDE", "EXTRA"}
    )
    private String productType;

    private Boolean isBurgerIngredient;

    @Schema(description = "ID de la categoría del producto", example = "2")
    private Integer productCategoryId;

    @Schema(description = "ID del proveedor", example = "1")
    private Integer supplierId;
}
