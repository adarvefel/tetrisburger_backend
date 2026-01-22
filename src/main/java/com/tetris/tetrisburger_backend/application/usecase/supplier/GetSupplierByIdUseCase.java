package com.tetris.tetrisburger_backend.application.usecase.supplier;

import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.GetSupplierById;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.query.GetSupplierByIdQuery;
import com.tetris.tetrisburger_backend.domain.port.out.SupplierRepository;
import org.springframework.stereotype.Service;

@Service
public class GetSupplierByIdUseCase implements GetSupplierById {
    private final SupplierRepository repo;

    public GetSupplierByIdUseCase(SupplierRepository repo) {
        this.repo = repo;
    }

    @Override
    public Supplier get(GetSupplierByIdQuery query) {
        return repo.findById(query.id()).orElseThrow(() -> new IllegalArgumentException("Supplier not found: " + query.id()));
    }
}