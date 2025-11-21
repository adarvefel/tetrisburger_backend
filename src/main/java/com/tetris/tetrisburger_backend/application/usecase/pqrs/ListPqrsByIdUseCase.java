package com.tetris.tetrisburger_backend.application.usecase.pqrs;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.ListPqrsById;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.query.ListPqrsByIdQuery;
import com.tetris.tetrisburger_backend.domain.port.out.PqrsPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ListPqrsByIdUseCase implements ListPqrsById {

    private final PqrsPort pqrsPort;

    public ListPqrsByIdUseCase(PqrsPort pqrsPort) {
        this.pqrsPort = pqrsPort;
    }

    @Override
    public PageResponse<Pqrs> handle(ListPqrsByIdQuery listPqrsByIdQuery, Integer idUser) {

        PageResponse<Pqrs> pqrs = pqrsPort.findAllById(listPqrsByIdQuery, idUser);

        return pqrs;
    }
}
