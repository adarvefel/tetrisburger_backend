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
        if (repo.existsByName(cmd.name().trim())){
            throw  new IllegalArgumentException("Este proveedor ya existe:"+ cmd.name());

        }
        Supplier s = Supplier.ofNew(
                cmd.name(), cmd.phone(), cmd.email().trim(), cmd.address(), cmd.registrationDate());
        return repo.save(s);
    }
}
