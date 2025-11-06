package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {


    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name", qualifiedByName = "trim")
    @Mapping(source = "description", target = "description", qualifiedByName = "trim")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "availability", target = "availability", qualifiedByName = "nullToFalse")
    @Mapping(source = "productType", target = "productType", qualifiedByName = "trim")
    @Mapping(source = "ingredientType", target = "ingredientType", qualifiedByName = "trim")
    @Mapping(source = "burgerIngredient", target = "burgerIngredient", qualifiedByName = "nullToFalse")
    @Mapping(source = "productCategoryId", target = "productCategoryId")
    @Mapping(source = "supplierId", target = "supplierId")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "deletedAt", target = "deletedAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy", target = "updatedBy")
    @Mapping(source = "deletedBy", target = "deletedBy")
    Product toDomain(ProductEntity entity);

    // ============================================
    // Domain → Entity
    // ============================================
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name", qualifiedByName = "trim")
    @Mapping(source = "description", target = "description", qualifiedByName = "trim")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "availability", target = "availability", qualifiedByName = "boolToBoolean")
    @Mapping(source = "productType", target = "productType", qualifiedByName = "trim")
    @Mapping(source = "ingredientType", target = "ingredientType", qualifiedByName = "trim")
    @Mapping(source = "burgerIngredient", target = "burgerIngredient", qualifiedByName = "boolToBoolean")
    @Mapping(source = "productCategoryId", target = "productCategoryId")
    @Mapping(source = "supplierId", target = "supplierId")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "deletedAt", target = "deletedAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy", target = "updatedBy")
    @Mapping(source = "deletedBy", target = "deletedBy")
    ProductEntity toEntity(Product domain);

    List<Product> toDomainList(List<ProductEntity> entities);

    List<ProductEntity> toEntityList(List<Product> domains);


    @Named("nullToFalse")
    default boolean nullToFalse(Boolean value) {
        return Boolean.TRUE.equals(value);
    }

    @Named("boolToBoolean")
    default Boolean boolToBoolean(boolean value) {
        return value;
    }

    @Named("trim")
    default String trim(String s) {
        return s == null ? null : s.trim();
    }
}