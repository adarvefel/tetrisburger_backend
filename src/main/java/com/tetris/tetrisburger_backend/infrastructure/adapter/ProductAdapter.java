package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.enums.ProductType;
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
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
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
    public boolean existsByNameIgnoreCaseAndDeletedAtIsNull(String name) {
        boolean exists = jpa.existsByNameIgnoreCaseAndDeletedAtIsNull(name);
        log.info("Verificando si existe producto con nombre '{}': {}", name, exists);
        return exists;
    }

    @Override
    public boolean existsByNameIgnoreCaseAndDeletedAtIsNullAndIdNot(String name, Integer id) {
        return jpa.existsByNameIgnoreCaseAndDeletedAtIsNullAndIdNot(name, id);
    }

    // ==================== LISTADO ADMIN ====================

    @Override
    public PageResponse<Product> findAll(Integer productCategoryId, Boolean availability, PaginationRequest page) {
        Specification<ProductEntity> spec = notDeleted()
                .and(withJoins())
                .and(byCategory(productCategoryId))
                .and(byAvailability(availability));

        Pageable pageable = toPageable(page);
        Page<ProductEntity> productPage = jpa.findAll(spec, pageable);

        return new PageResponse<>(
                productPage.getContent().stream().map(mapper::toDomain).toList(),
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages()
        );
    }

    // ==================== BÚSQUEDA ====================

    @Override
    public PageResponse<Product> search(
            String q,
            Integer productCategoryId,
            Boolean availability,
            ProductType productType,
            PaginationRequest pageReq
    ) {
        Pageable pageable = toPageable(pageReq);
        String normalizedQuery = (q != null && q.trim().isEmpty()) ? null : q;

        Page<ProductEntity> page = jpa.searchProducts(
                normalizedQuery,
                productCategoryId,
                availability,
                productType,
                pageable
        );

        return toPageResponse(page);
    }

    // ==================== INGREDIENTES ====================

    @Override
    public PageResponse<Product> findAllBurgerIngredients(Integer categoryId, PaginationRequest page) {
        Page<ProductEntity> result = jpa.findAllBurgerIngredients(ProductType.INGREDIENT, categoryId, toPageable(page));
        return toPageResponse(result);
    }

    @Override
    public PageResponse<Product> searchIngredients(String name, PaginationRequest pagination) {
        Page<ProductEntity> page = jpa.searchIngredients(ProductType.INGREDIENT, name, toPageable(pagination));
        return toPageResponse(page);
    }


    // ==================== VISTA PÚBLICA ====================

    @Override
    public PageResponse<Product> findPublicProducts(ProductType productType, Integer categoryId, PaginationRequest pageReq) {
        List<ProductType> types = (productType != null)
                ? List.of(productType)
                : List.of(ProductType.SIDE, ProductType.BEVERAGE);

        Page<ProductEntity> page = jpa.findByProductTypeIn(types, categoryId, toPageable(pageReq));
        return toPageResponse(page);
    }

    // ==================== SPECIFICATIONS ====================

    private Specification<ProductEntity> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    private Specification<ProductEntity> withJoins() {
        return (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("productCategory", JoinType.LEFT);
                root.fetch("supplier", JoinType.LEFT);
            }
            return cb.conjunction();
        };
    }

    private Specification<ProductEntity> byCategory(Integer categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) return cb.conjunction();
            return cb.equal(root.get("productCategory").get("id"), categoryId);
        };
    }

    private Specification<ProductEntity> byAvailability(Boolean availability) {
        return (root, query, cb) -> availability == null
                ? cb.conjunction()
                : cb.equal(root.get("availability"), availability);
    }

    private Specification<ProductEntity> excludeIngredients() {
        return (root, query, cb) ->
                cb.notEqual(root.get("productType"), ProductType.INGREDIENT);
    }

    // ==================== HELPERS ====================

    private Pageable toPageable(PaginationRequest pageReq) {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        String sortBy = pageReq.getSortBy();
        String dir = (pageReq.getDirection() == null || pageReq.getDirection().isBlank())
                ? "ASC"
                : pageReq.getDirection().trim().toUpperCase();

        if (sortBy != null && !sortBy.isBlank()) {
            sort = "DESC".equals(dir)
                    ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();
        }

        return PageRequest.of(pageReq.getPage(), pageReq.getSize(), sort);
    }

    @Override
    public Optional<Product> findByIdForUpdate(Integer id) {
        return jpa.findByIdForUpdate(id).map(mapper::toDomain);
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


