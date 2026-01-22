package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Supplier;

import java.util.Optional;

public interface SupplierRepository {
    Supplier save(Supplier supplier);

    Optional<Supplier> findById(Integer id);

    void deleteById(Integer id);

    boolean existsByEmailIgnoreCase(String email);

    PageResponse<Supplier> findAll(String q, PaginationRequest page);
}