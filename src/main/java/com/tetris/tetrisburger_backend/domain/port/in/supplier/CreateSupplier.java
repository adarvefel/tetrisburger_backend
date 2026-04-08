package com.tetris.tetrisburger_backend.domain.port.in.supplier;

import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.command.CreateSupplierCommand;

public interface CreateSupplier {
    Supplier create(CreateSupplierCommand cmd);
}