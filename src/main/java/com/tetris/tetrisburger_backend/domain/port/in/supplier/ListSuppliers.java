package com.tetris.tetrisburger_backend.domain.port.in.supplier;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.query.ListSuppliersQuery;

public interface ListSuppliers {
    PageResponse<Supplier> list(ListSuppliersQuery query, PaginationRequest page);
}
