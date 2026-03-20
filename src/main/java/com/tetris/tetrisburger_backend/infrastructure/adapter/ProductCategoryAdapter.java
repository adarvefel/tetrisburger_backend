package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.model.ProductType;
import com.tetris.tetrisburger_backend.domain.port.out.ProductCategoryRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.ProductCategoryEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.ProductCategoryEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.ProductCategoryJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductCategoryAdapter implements ProductCategoryRepository {

    private final ProductCategoryJpaRepository jpa;
    private final ProductCategoryEntityMapper mapper;

    public ProductCategoryAdapter(ProductCategoryJpaRepository jpa, ProductCategoryEntityMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public ProductCategory save(ProductCategory category) {
        if (category.getAvailable() == null) category.setAvailable(Boolean.TRUE);
        ProductCategoryEntity saved = jpa.save(mapper.toEntity(category));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ProductCategory> findById(Integer id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(Integer id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return jpa.existsByNameIgnoreCase(name);
    }

    @Override
    public PageResponse<ProductCategory> findAll(String nameContains, PaginationRequest pageReq) {
        Specification<ProductCategoryEntity> spec = Specification.unrestricted(); // reemplaza where(null)
        if (nameContains != null && !nameContains.isBlank()) {
            String pattern = "%" + nameContains.trim().toLowerCase() + "%";
            spec = spec.and((root, cq, cb) -> cb.like(cb.lower(root.get("name")), pattern));
        }
        Pageable pageable = toPageable(pageReq);
        Page<ProductCategoryEntity> page = jpa.findAll(spec, pageable);
        return new PageResponse<>(
                page.map(mapper::toDomain).getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private Pageable toPageable(PaginationRequest pageReq) {
        String sortBy = pageReq.getSortBy();
        String dir = (pageReq.getDirection() == null || pageReq.getDirection().isBlank())
                ? "ASC" : pageReq.getDirection().trim().toUpperCase();
        if (sortBy == null || sortBy.isBlank()) sortBy = "name";
        Sort sort = "DESC".equals(dir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return PageRequest.of(pageReq.getPage(), pageReq.getSize(), sort);
    }

    @Override
    public List<ProductCategory> findPublicCategories() {
        List<ProductType> publicTypes = List.of(ProductType.SIDE, ProductType.BEVERAGE);
        return jpa.findCategoriesWithPublicProducts(publicTypes)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }



}