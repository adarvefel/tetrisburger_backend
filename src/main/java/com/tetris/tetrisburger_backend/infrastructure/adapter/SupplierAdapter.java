package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.domain.port.out.SupplierRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.SupplierEntity;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.SupplierEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.SupplierJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SupplierAdapter implements SupplierRepository {

    private final SupplierJpaRepository jpa;
    private final SupplierEntityMapper mapper;

    public SupplierAdapter(SupplierJpaRepository jpa, SupplierEntityMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Supplier save(Supplier supplier) {
        SupplierEntity saved = jpa.save(mapper.toEntity(supplier));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Supplier> findById(Integer id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(Integer id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return jpa.existsByEmailIgnoreCase(email);
    }

    @Override
    public PageResponse<Supplier> findAll(String q, PaginationRequest pageReq) {
        Specification<SupplierEntity> spec = Specification.unrestricted();
        if (q != null && !q.isBlank()) {
            String pattern = "%" + q.trim().toLowerCase() + "%";
            spec = spec.and((root, cq, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern)
            ));
        }
        Pageable pageable = toPageable(pageReq);
        Page<SupplierEntity> page = jpa.findAll(spec, pageable);
        return new PageResponse<>(
                page.map(mapper::toDomain).getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    private Pageable toPageable(PaginationRequest pr) {
        String sortBy = (pr.getSortBy() == null || pr.getSortBy().isBlank()) ? "name" : pr.getSortBy();
        String dir = (pr.getDirection() == null ? "ASC" : pr.getDirection().trim().toUpperCase());
        Sort sort = "DESC".equals(dir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return PageRequest.of(pr.getPage(), pr.getSize(), sort);
    }
}
