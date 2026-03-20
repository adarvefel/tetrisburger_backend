package com.tetris.tetrisburger_backend.domain.port.in.supplier;

import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.command.UpdateSupplierCommand;

public interface UpdateSupplier {
    Supplier update(UpdateSupplierCommand cmd);
}
