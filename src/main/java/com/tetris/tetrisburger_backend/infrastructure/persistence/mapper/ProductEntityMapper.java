// src/main/java/com/tetris/tetrisburger_backend/infrastructure/persistence/mapper/ProductEntityMapper.java
package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.ProductEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.ProductCategoryEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.SupplierEntity;
import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {ProductCategoryEntityMapper.class})
public interface ProductEntityMapper {

    Logger logger = LoggerFactory.getLogger(ProductEntityMapper.class);

    // ==================== Entity -> Domain ====================

    default Product toDomain(ProductEntity e) {
        if (e == null) return null;

        ProductCategory category = null;
        try {
            if (e.getProductCategory() != null) {
                ProductCategoryEntity catEntity = e.getProductCategory();
                category = ProductCategory.of(
                        catEntity.getId(),
                        catEntity.getName(),
                        catEntity.getDescription(),
                        catEntity.getAvailable()
                );
            }
        } catch (jakarta.persistence.EntityNotFoundException ex) {
            logger.warn(" Categoría no encontrada para producto ID: {}", e.getId());
        }

        Supplier supplier = null;
        if (e.getSupplier() != null){
            SupplierEntity supplEntity = e.getSupplier();
            supplier = Supplier.of(
                    supplEntity.getId(),
                    supplEntity.getName(),
                    supplEntity.getPhone(),
                    supplEntity.getEmail(),
                    supplEntity.getAddress(),
                    supplEntity.getRegistrationDate()
            );
        }

        return Product.of(
                e.getId(),
                trim(e.getName()),
                trim(e.getDescription()),
                e.getQuantity(),
                e.getPrice(),
                e.getAvailability(),
                e.getProductType(),
                e.getIngredientBurger(),
                category,
                trim(e.getImageUrl()),
                trim(e.getImageKey()),
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
        e.setName(trim(d.getName()));
        e.setDescription(trim(d.getDescription()));
        e.setQuantity(d.getQuantity());
        e.setPrice(d.getPrice());
        e.setAvailability(d.getAvailability());
        e.setProductType(d.getProductType());
        e.setIngredientBurger(d.getIsBurgerIngredient());

        if (d.getProductCategory() != null) {
            ProductCategoryEntity catEntity = new ProductCategoryEntity();
            catEntity.setId(d.getProductCategory().getId());
            catEntity.setName(d.getProductCategory().getName());
            catEntity.setDescription(d.getProductCategory().getDescription());
            catEntity.setAvailable(d.getProductCategory().getAvailable());
            e.setProductCategory(catEntity);
        }

        e.setImageUrl(trim(d.getImageUrl()));
        e.setImageKey(trim(d.getImageKey()));

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

    // ==================== Utils ====================

    default String trim(String s) {
        return s == null ? null : s.trim();
    }
}
