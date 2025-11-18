package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.query.ListPqrsQuery;

import java.util.Optional;

public interface PqrsPort {

    Pqrs savePqrs(Pqrs pqrs);

    Optional<Pqrs> findById(Integer id);

    PageResponse<Pqrs> findAllPqrs(ListPqrsQuery listPqrsQuery);

    void softDeletePqrs(Integer idPqrs, Integer idUser);
}
