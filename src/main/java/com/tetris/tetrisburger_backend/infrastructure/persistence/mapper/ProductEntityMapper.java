package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.ProductEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.ProductCategoryEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.SupplierEntity;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {ProductCategoryEntityMapper.class, StringMapperHelper.class})
public interface ProductEntityMapper {

    // ==================== Entity -> Domain ====================

    default Product toDomain(ProductEntity e) {
        if (e == null) return null;

        ProductCategory category = null;
        if (e.getProductCategory() != null) {
            ProductCategoryEntity cat = e.getProductCategory();
            category = ProductCategory.reconstitute(
                    cat.getId(),
                    cat.getName(),
                    cat.getDescription(),
                    cat.getAvailable(),
                    cat.getCreatedAt(),
                    cat.getUpdatedAt(),
                    cat.getDeletedAt(),
                    cat.getCreatedBy(),
                    cat.getUpdatedBy(),
                    cat.getDeletedBy()
            );
        }

        Supplier supplier = null;
        if (e.getSupplier() != null) {
            SupplierEntity sup = e.getSupplier();
            supplier = Supplier.reconstitute(
                    sup.getId(),
                    sup.getName(),
                    sup.getPhone(),
                    sup.getEmail(),
                    sup.getAddress(),
                    sup.getRegistrationDate(),
                    sup.getUpdatedAt(),
                    sup.getDeletedAt(),
                    sup.getCreatedBy(),
                    sup.getUpdatedBy(),
                    sup.getDeletedBy()
            );
        }

        return Product.of(
                e.getId(),
                e.getName(),
                e.getDescription(),
                e.getQuantity(),
                e.getPrice(),
                e.getAvailability(),
                e.getProductType(),
                category,
                e.getImageUrl(),
                e.getImageKey(),
                supplier,
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getDeletedAt(),
                e.getCreatedBy(),
                e.getUpdatedBy(),
                e.getDeletedBy()
        );
    }

    // ==================== Domain -> Entity ====================

    default ProductEntity toEntity(Product d) {
        if (d == null) return null;

        ProductEntity e = new ProductEntity();
        e.setId(d.getId());
        e.setName(d.getName());
        e.setDescription(d.getDescription());
        e.setQuantity(d.getQuantity());
        e.setPrice(d.getPrice());
        e.setAvailability(d.getAvailability());
        e.setProductType(d.getProductType());
        e.setIsBurgerIngredient(d.isBurgerIngredient());

        if (d.getProductCategory() != null) {
            ProductCategoryEntity cat = new ProductCategoryEntity();
            cat.setId(d.getProductCategory().getId());
            cat.setName(d.getProductCategory().getName());
            cat.setDescription(d.getProductCategory().getDescription());
            cat.setAvailable(d.getProductCategory().getAvailable());
            cat.setCreatedAt(d.getProductCategory().getCreatedAt());
            cat.setUpdatedAt(d.getProductCategory().getUpdatedAt());
            cat.setDeletedAt(d.getProductCategory().getDeletedAt());
            cat.setCreatedBy(d.getProductCategory().getCreatedBy());
            cat.setUpdatedBy(d.getProductCategory().getUpdatedBy());
            cat.setDeletedBy(d.getProductCategory().getDeletedBy());
            e.setProductCategory(cat);
        }

        e.setImageUrl(d.getImageUrl());
        e.setImageKey(d.getImageKey());

        if (d.getSupplier() != null && d.getSupplier().getId() != null) {
            SupplierEntity sup = new SupplierEntity();
            sup.setId(d.getSupplier().getId());
            e.setSupplier(sup);
        } else {
            e.setSupplier(null);
        }

        e.setCreatedAt(d.getCreatedAt());
        e.setUpdatedAt(d.getUpdatedAt());
        e.setDeletedAt(d.getDeletedAt());
        e.setCreatedBy(d.getCreatedBy());
        e.setUpdatedBy(d.getUpdatedBy());
        e.setDeletedBy(d.getDeletedBy());

        return e;
    }

    // ==================== List helpers ====================

    default List<Product> toDomainList(List<ProductEntity> entities) {
        return entities == null ? List.of() :
                entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    default List<ProductEntity> toEntityList(List<Product> domains) {
        return domains == null ? List.of() :
                domains.stream().map(this::toEntity).collect(Collectors.toList());
    }
}