package com.tetris.tetrisburger_backend.application.usecase.supplier;

import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.UpdateSupplier;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.command.UpdateSupplierCommand;
import com.tetris.tetrisburger_backend.domain.port.out.SupplierRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UpdateSupplierUseCase implements UpdateSupplier {
    private final SupplierRepository repo;

    public UpdateSupplierUseCase(SupplierRepository repo) {
        this.repo = repo;
    }

    @Override
    public Supplier update(UpdateSupplierCommand cmd) {
        Supplier current = repo.findById(cmd.id())
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado: " + cmd.id()));

        current.update(
                cmd.name(),
                cmd.phone(),
                cmd.email(),
                cmd.address(),
                cmd.updatedBy()  // ← registrationDate eliminado, updatedBy agregado
        );
        return repo.save(current);
    }
}
