package com.tetris.tetrisburger_backend.domain.port.in.supplier;

import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.query.GetSupplierByIdQuery;

public interface GetSupplierById {
    Supplier get(GetSupplierByIdQuery query);
}
