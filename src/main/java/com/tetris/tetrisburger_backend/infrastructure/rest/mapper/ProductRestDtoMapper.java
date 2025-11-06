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
                dto.getName(),
                dto.getDescription(),
                dto.getQuantity(),
                dto.getPrice(),
                Boolean.TRUE.equals(dto.getAvailability()),
                dto.getProductType(),
                dto.getIngredientType(),
                Boolean.TRUE.equals(dto.getBurgerIngredient()),
                dto.getProductCategoryId(),
                dto.getSupplierId(),
                createdBy
        );
    }


    default UpdateProductCommand toUpdateProductCommand(Integer idProduct, UpdateProductRequestDTO dto, Integer updatedBy) {
        if (dto == null) return null;
        return new UpdateProductCommand(
                idProduct,
                dto.getName(),
                dto.getDescription(),
                dto.getQuantity(),
                dto.getPrice(),
                dto.getAvailability(),
                dto.getProductType(),
                dto.getIngredientType(),
                dto.getBurgerIngredient(),
                dto.getProductCategoryId(),
                dto.getSupplierId(),
                updatedBy
        );
    }

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "availability", target = "availability")
    @Mapping(source = "productType", target = "productType")
    @Mapping(source = "ingredientType", target = "ingredientType")
    @Mapping(source = "burgerIngredient", target = "burgerIngredient")
    @Mapping(source = "productCategoryId", target = "productCategoryId")
    @Mapping(source = "supplierId", target = "supplierId")
    ProductResponseDTO toProductResponseDTO(Product product);

    // Lista de productos → lista de DTOs
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "availability", target = "availability")
    @Mapping(source = "productType", target = "productType")
    @Mapping(source = "ingredientType", target = "ingredientType")
    @Mapping(source = "burgerIngredient", target = "burgerIngredient")
    @Mapping(source = "productCategoryId", target = "productCategoryId")
    @Mapping(source = "supplierId", target = "supplierId")
    List<ProductResponseDTO> toProductResponseDTOList(List<Product> products);

    // PageResponse<Product> → ListProductResponseDTO
    default ListProductResponseDTO toListProductResponseDTO(PageResponse<Product> pageResponse) {
        if (pageResponse == null) return null;


        List<ProductResponseDTO> items = toProductResponseDTOList(pageResponse.content());


        return ListProductResponseDTO.builder()
                .items(items)
                .page(pageResponse.page())
                .size(pageResponse.size())
                .totalElements(pageResponse.totalElements())
                .totalPages(pageResponse.totalPages())
                .build();
    }
}
