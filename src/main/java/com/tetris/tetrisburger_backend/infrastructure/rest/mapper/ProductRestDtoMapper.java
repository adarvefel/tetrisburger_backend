package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.model.ProductType;
import com.tetris.tetrisburger_backend.domain.port.in.product.command.CreateProductCommand;
import com.tetris.tetrisburger_backend.domain.port.in.product.command.UpdateProductCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory.ProductCategoryResponseDTO;
import org.mapstruct.Mapper;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductRestDtoMapper {

    // ==================== CREATE ====================

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
                ProductType.valueOf(dto.getProductType()),
                dto.getIsBurgerIngredient(),
                imageData,
                dto.getProductCategoryId(),
                dto.getSupplierId(),
                createdBy
        );
    }

    // ==================== UPDATE ====================

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
                ProductType.valueOf(dto.getProductType()),
                dto.getIsBurgerIngredient(),
                dto.getProductCategoryId(),
                dto.getSupplierId(),
                updatedBy
        );
    }

    // ==================== RESPONSE (Productos Activos) ====================

    default ProductResponseDTO toProductResponseDTO(Product product) {
        if (product == null) return null;

        ProductCategoryResponseDTO categoryDTO = null;
        if (product.getProductCategory() != null) {
            categoryDTO = ProductCategoryResponseDTO.builder()
                    .id(product.getProductCategory().getId())
                    .name(product.getProductCategory().getName())
                    .description(product.getProductCategory().getDescription())
                    .available(product.getProductCategory().getAvailable())
                    .build();
        }

        return ProductResponseDTO.builder()
                .idProduct(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .quantity(product.getQuantity())
                .price(product.getPrice())
                .availability(product.getAvailability())
                .productType(product.getProductType())  // ✅ ProductType (ENUM)
                .productCategory(categoryDTO)  // ✅ Objeto completo
                .supplierId(product.getSupplierId())
                .imageUrl(product.getImageUrl())
                .imageStatus(null)  // Se setea en controller
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .createdBy(product.getCreatedBy())
                .updatedBy(product.getUpdatedBy())
                .build();
    }

    default List<ProductResponseDTO> toProductResponseDTOList(List<Product> products) {
        if (products == null) return List.of();
        return products.stream()
                .map(this::toProductResponseDTO)
                .collect(Collectors.toList());
    }

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

    default DeleteProductResponseDTO toDeletedProductResponseDTO(Product product) {
        if (product == null) return null;

        ProductCategoryResponseDTO categoryDTO = null;
        if (product.getProductCategory() != null) {
            categoryDTO = ProductCategoryResponseDTO.builder()
                    .id(product.getProductCategory().getId())
                    .name(product.getProductCategory().getName())
                    .description(product.getProductCategory().getDescription())
                    .available(product.getProductCategory().getAvailable())
                    .build();
        }

        return DeleteProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .quantity(product.getQuantity())
                .price(product.getPrice())
                .availability(product.getAvailability())
                .productType(product.getProductType().name())
                .productCategory(categoryDTO)
                .supplierId(product.getSupplierId())
                .imageUrl(product.getImageUrl())
                .imageStatus(null)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .deletedAt(product.getDeletedAt())
                .createdBy(product.getCreatedBy())
                .updatedBy(product.getUpdatedBy())
                .deletedBy(product.getDeletedBy())
                .build();
    }

    default List<DeleteProductResponseDTO> toDeletedProductResponseDTOList(List<Product> products) {
        if (products == null) return List.of();
        return products.stream()
                .map(this::toDeletedProductResponseDTO)
                .collect(Collectors.toList());
    }
}
