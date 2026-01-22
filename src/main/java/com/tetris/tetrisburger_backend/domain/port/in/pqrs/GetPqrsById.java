package com.tetris.tetrisburger_backend.domain.port.in.pqrs;

import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.query.GetPqrsByIdQuery;
import com.tetris.tetrisburger_backend.domain.port.in.product.query.GetProductByIdQuery;

public interface GetPqrsById {
    Pqrs handle(GetPqrsByIdQuery getPqrsByIdQuery);
}
