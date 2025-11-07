package com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateProductCategoryRequestDTO {
    @NotBlank
    @Size(max = 255)
    private String name;
    @Size(max = 255)
    private String description;
    @NotNull
    private Boolean available;
}