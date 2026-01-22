package com.tetris.tetrisburger_backend.infrastructure.adapter;


import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.ProductEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.ProductEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.ProductJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductAdapter implements ProductRepository {

    private final ProductJpaRepository jpa;
    private final ProductEntityMapper mapper;

    public ProductAdapter(ProductJpaRepository jpa, ProductEntityMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Product save(Product product) {
        ProductEntity saved = jpa.save(mapper.toEntity(product));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Product> findById(Integer id) {
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
    public PageResponse<Product> findAll(Integer productCategoryId, Boolean availability, PaginationRequest pageReq) {
        Specification<ProductEntity> spec = notDeleted(); // sin Specification.where()

        if (productCategoryId != null) {
            spec = spec.and(eqCategory(productCategoryId));
        }
        if (availability != null) {
            spec = spec.and(eqAvailability(availability));
        }

        Pageable pageable = toPageable(pageReq);
        Page<ProductEntity> page = jpa.findAll(spec, pageable);
        return toPageResponse(page);
    }

    @Override
    public PageResponse<Product> search(String q, Integer productCategoryId, Boolean availability, PaginationRequest pageReq) {
        Specification<ProductEntity> spec = notDeleted(); // sin Specification.where()

        if (q != null && !q.isBlank()) {
            spec = spec.and(likeNameOrDescription(q));
        }
        if (productCategoryId != null) {
            spec = spec.and(eqCategory(productCategoryId));
        }
        if (availability != null) {
            spec = spec.and(eqAvailability(availability));
        }

        Pageable pageable = toPageable(pageReq);
        Page<ProductEntity> page = jpa.findAll(spec, pageable);
        return toPageResponse(page);
    }

    private Pageable toPageable(PaginationRequest pageReq) {
        // Orden por defecto
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        String sortBy = pageReq.getSortBy();
        String dir = (pageReq.getDirection() == null || pageReq.getDirection().isBlank())
                ? "ASC"
                : pageReq.getDirection().trim().toUpperCase();
        if (sortBy != null && !sortBy.isBlank()) {
            sort = "DESC".equals(dir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        }
        return PageRequest.of(pageReq.getPage(), pageReq.getSize(), sort);
    }

    private PageResponse<Product> toPageResponse(Page<ProductEntity> page) {
        return new PageResponse<>(
                page.map(mapper::toDomain).getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private Specification<ProductEntity> notDeleted() {
        return (root, cq, cb) -> cb.isNull(root.get("deletedAt"));
    }

    private Specification<ProductEntity> eqCategory(Integer categoryId) {
        return (root, cq, cb) -> cb.equal(root.get("productCategoryId"), categoryId);
    }

    private Specification<ProductEntity> eqAvailability(boolean availability) {
        return (root, cq, cb) -> cb.equal(root.get("availability"), availability);
    }

    private Specification<ProductEntity> likeNameOrDescription(String q) {
        String pattern = "%" + q.toLowerCase() + "%";
        return (root, cq, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("description")), pattern)
        );
    }
}
