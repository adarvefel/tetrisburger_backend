package com.tetris.tetrisburger_backend.application.usecase.supplier;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.ListSuppliers;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.query.ListSuppliersQuery;
import com.tetris.tetrisburger_backend.domain.port.out.SupplierRepository;
import org.springframework.stereotype.Service;

@Service
public class ListSuppliersUseCase implements ListSuppliers {
    private final SupplierRepository repo;

    public ListSuppliersUseCase(SupplierRepository repo) {
        this.repo = repo;
    }

    @Override
    public PageResponse<Supplier> list(ListSuppliersQuery query, PaginationRequest page) {
        return repo.findAll(query.q(), page);
    }
}
