package com.tetris.tetrisburger_backend.application.usecase.supplier;

import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.CreateSupplier;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.command.CreateSupplierCommand;
import com.tetris.tetrisburger_backend.domain.port.out.SupplierRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CreateSupplierUseCase implements CreateSupplier {
    private final SupplierRepository repo;

    public CreateSupplierUseCase(SupplierRepository repo) {
        this.repo = repo;
    }

    @Override
    public Supplier create(CreateSupplierCommand cmd) {

        if (cmd.email() != null && !cmd.email().isBlank() &&
                repo.existsByEmailIgnoreCaseAndDeletedAtIsNull(cmd.email().trim())) {
            throw new IllegalArgumentException(
                    "Ya existe un proveedor con el email: " + cmd.email());
        }


        Supplier s = Supplier.ofNew(
                cmd.name(), cmd.phone(), cmd.email().trim(), cmd.address(), cmd.registrationDate());
        return repo.save(s);
    }
}
