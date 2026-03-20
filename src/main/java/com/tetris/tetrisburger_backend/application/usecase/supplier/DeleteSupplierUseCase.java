package com.tetris.tetrisburger_backend.application.usecase.supplier;

import com.tetris.tetrisburger_backend.domain.port.in.supplier.DeleteSupplier;
import com.tetris.tetrisburger_backend.domain.port.out.SupplierRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DeleteSupplierUseCase implements DeleteSupplier {
    private final SupplierRepository repo;

    public DeleteSupplierUseCase(SupplierRepository repo) {
        this.repo = repo;
    }

    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
    }
}