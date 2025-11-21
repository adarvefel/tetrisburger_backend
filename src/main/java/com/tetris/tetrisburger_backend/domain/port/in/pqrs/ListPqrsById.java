package com.tetris.tetrisburger_backend.domain.port.in.pqrs;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.query.ListPqrsByIdQuery;


public interface ListPqrsById {
    PageResponse<Pqrs> handle(ListPqrsByIdQuery listPqrsByIdQuery, Integer idUser);
}
