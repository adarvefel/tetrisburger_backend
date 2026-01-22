// src/main/java/com/tetris/tetrisburger_backend/infrastructure/rest/mapper/ProductRestDtoMapper.java
package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.command.CreateProductCommand;
import com.tetris.tetrisburger_backend.domain.port.in.product.command.UpdateProductCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.CreateProductRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.ListProductResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.ProductResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.UpdateProductRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductRestDtoMapper {

    default CreateProductCommand toCreateProductCommand(CreateProductRequestDTO dto, Integer createdBy) {
        if (dto == null) return null;
        return new CreateProductCommand(
                dto.getName(), dto.getDescription(), dto.getQuantity(), dto.getPrice(),
                Boolean.TRUE.equals(dto.getAvailability()),
                dto.getProductType(), dto.getIngredientType(), Boolean.TRUE.equals(dto.getBurgerIngredient()),
                dto.getImageUrl(), dto.getProductCategoryId(), dto.getSupplierId(), createdBy
        );
    }

    default UpdateProductCommand toUpdateProductCommand(Integer idProduct, UpdateProductRequestDTO dto, Integer updatedBy) {
        if (dto == null) return null;
        return new UpdateProductCommand(
                idProduct, dto.getName(), dto.getDescription(), dto.getQuantity(), dto.getPrice(),
                dto.getAvailability(), dto.getProductType(), dto.getIngredientType(), dto.getBurgerIngredient(),
                dto.getImageUrl(), dto.getProductCategoryId(), dto.getSupplierId(), updatedBy
        );
    }

    @Mapping(source = "imageUrl", target = "imageUrl")
    ProductResponseDTO toProductResponseDTO(Product product);

    List<ProductResponseDTO> toProductResponseDTOList(List<Product> products);

    default ListProductResponseDTO toListProductResponseDTO(PageResponse<Product> page) {
        if (page == null) return null;
        return ListProductResponseDTO.builder()
                .items(toProductResponseDTOList(page.content()))
                .page(page.page())
                .size(page.size())
                .totalElements(page.totalElements())
                .totalPages(page.totalPages())
                .build();
    }
}
