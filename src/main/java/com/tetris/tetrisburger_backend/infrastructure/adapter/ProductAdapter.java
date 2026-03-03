package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.ProductEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.ProductEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.ProductJpaRepository;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductAdapter implements ProductRepository {

    private final Logger log = LoggerFactory.getLogger(ProductAdapter.class);

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
    public PageResponse<Product> findAll(Integer productCategoryId, Boolean availability, PaginationRequest page) {
        Specification<ProductEntity> spec = notDeleted()
                .and(withCategory())
                .and(byCategory(productCategoryId))
                .and(byAvailability(availability));

        Pageable pageable = toPageable(page);
        Page<ProductEntity> productPage = jpa.findAll(spec, pageable);

        List<Product> products = productPage.getContent().stream()
                .map(mapper::toDomain)
                .toList();

        return new PageResponse<>(
                products,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages()
        );
    }


    @Override
    public PageResponse<Product> search(String q, Integer productCategoryId, Boolean availability, PaginationRequest pageReq) {
        Pageable pageable = toPageable(pageReq);
        String normalizedQuery = (q != null && q.trim().isEmpty()) ? null : q;

        Page<ProductEntity> page = jpa.searchProducts(
                normalizedQuery,
                productCategoryId,
                availability,
                pageable
        );

        return toPageResponse(page);
    }



    @Override
    public boolean existsByNameIgnoreCase(String name) {
        boolean exists = jpa.existsByNameIgnoreCaseAndDeletedAtIsNull(name);
        log.info(" Verificando si existe producto con nombre '{}': {}", name, exists);
        return exists;
    }

    // ========== Specifications ==========

    private Specification<ProductEntity> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    // NUEVO: JOIN FETCH para productCategory
    private Specification<ProductEntity> withCategory() {
        return (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("productCategory", JoinType.LEFT);
            }
            return cb.conjunction();
        };
    }

    private Specification<ProductEntity> byCategory(Integer categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.join("productCategory", JoinType.LEFT).get("id"), categoryId);
        };
    }

    private Specification<ProductEntity> byAvailability(Boolean availability) {
        return (root, query, cb) -> availability == null ?
                cb.conjunction() :
                cb.equal(root.get("availability"), availability);
    }

    private Specification<ProductEntity> likeNameOrDescription(String q) {
        String pattern = "%" + q.toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("description")), pattern)
        );
    }

    // ========== Helper Methods ==========

    private Pageable toPageable(PaginationRequest pageReq) {
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
}
