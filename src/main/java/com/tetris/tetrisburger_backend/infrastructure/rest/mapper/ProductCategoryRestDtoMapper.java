package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;


import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.command.CreateProductCategoryCommand;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.command.UpdateProductCategoryCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory.CreateProductCategoryRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory.ListProductCategoryResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory.ProductCategoryResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory.UpdateProductCategoryRequestDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductCategoryRestDtoMapper {

    default CreateProductCategoryCommand toCreateCommand(CreateProductCategoryRequestDTO dto) {
        if (dto == null) return null;
        Boolean available = dto.getAvailable() == null ? Boolean.TRUE : dto.getAvailable();
        return new CreateProductCategoryCommand(dto.getName(), dto.getDescription(), available);
    }

    default UpdateProductCategoryCommand toUpdateCommand(Integer id, UpdateProductCategoryRequestDTO dto) {
        if (dto == null) return null;
        Boolean available = dto.getAvailable() == null ? Boolean.TRUE : dto.getAvailable();
        return new UpdateProductCategoryCommand(id, dto.getName(), dto.getDescription(), available);
    }

    ProductCategoryResponseDTO toResponseDTO(ProductCategory category);

    List<ProductCategoryResponseDTO> toResponseDTOList(List<ProductCategory> categories);

    default ListProductCategoryResponseDTO toListResponseDTO(PageResponse<ProductCategory> page) {
        if (page == null) return null;
        List<ProductCategoryResponseDTO> items = toResponseDTOList(page.content());
        return ListProductCategoryResponseDTO.builder()
                .items(items)
                .page(page.page())
                .size(page.size())
                .totalElements(page.totalElements())
                .totalPages(page.totalPages())
                .build();
    }
}