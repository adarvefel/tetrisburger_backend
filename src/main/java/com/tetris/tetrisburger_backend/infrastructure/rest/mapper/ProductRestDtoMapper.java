package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.command.CreateProductCommand;
import com.tetris.tetrisburger_backend.domain.port.in.product.command.UpdateProductCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.*;
import org.mapstruct.Mapper;
import java.time.Instant;
import org.mapstruct.Mapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductRestDtoMapper {

    // ==================== CREATE ====================

    /**
     * Convierte DTO + MultipartFile → CreateProductCommand con FileData
     */
    default CreateProductCommand toCreateProductCommand(
            CreateProductRequestDTO dto,
            MultipartFile productImage,
            Integer createdBy) {

        if (dto == null) return null;

        FileData imageData = FileData.from(productImage);

        return new CreateProductCommand(
                dto.getName(),
                dto.getDescription(),
                dto.getQuantity(),
                dto.getPrice(),
                dto.getAvailability(),
                dto.getProductType(),
                dto.getIngredientType(),
                dto.getBurgerIngredient(),
                imageData,
                dto.getProductCategoryId(),
                dto.getSupplierId(),
                createdBy
        );
    }

    // ==================== UPDATE ====================

    /**
     * Convierte DTO → UpdateProductCommand (SIN imagen)
     */
    default UpdateProductCommand toUpdateProductCommand(
            Integer idProduct,
            UpdateProductRequestDTO dto,
            Integer updatedBy) {

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

    // ==================== RESPONSE (Productos Activos) ====================

    /**
     * Convierte Product → ProductResponseDTO (productos activos)
     * Incluye campos de auditoría básicos (sin deletedAt/deletedBy)
     */
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "imageStatus", ignore = true)
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy", target = "updatedBy")
    ProductResponseDTO toProductResponseDTO(Product product);

    /**
     * Convierte lista de Product → lista de ProductResponseDTO
     */
    List<ProductResponseDTO> toProductResponseDTOList(List<Product> products);

    /**
     * Convierte PageResponse de Product → ListProductResponseDTO
     */
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

    // ==================== RESPONSE (Productos Eliminados) ====================

    /**
     * Convierte Product → DeletedProductResponseDTO (productos eliminados)
     * Incluye auditoría completa con deletedAt y deletedBy
     */
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "imageStatus", ignore = true)
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "deletedAt", target = "deletedAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy", target = "updatedBy")
    @Mapping(source = "deletedBy", target = "deletedBy")
    DeleteProductResponseDTO toDeletedProductResponseDTO(Product product);

    /**
     * Convierte lista de Product → lista de DeletedProductResponseDTO
     */
    List<DeleteProductResponseDTO> toDeletedProductResponseDTOList(List<Product> products);
}
