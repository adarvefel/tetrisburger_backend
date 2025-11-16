package com.tetris.tetrisburger_backend.application.usecase.pqrs;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.ListPqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.query.ListPqrsQuery;
import com.tetris.tetrisburger_backend.domain.port.in.user.query.ListUsersQuery;
import com.tetris.tetrisburger_backend.domain.port.out.PqrsPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ListPqrsUseCase implements ListPqrs {

    private final PqrsPort pqrsPort;

    public ListPqrsUseCase(PqrsPort pqrsPort) {
        this.pqrsPort = pqrsPort;
    }

    @Override
    public PageResponse<Pqrs> handle(ListPqrsQuery listPqrsQuery) {
        PageResponse<Pqrs> pqrs = pqrsPort.findAllPqrs(listPqrsQuery);
        return pqrs;
    }
}
