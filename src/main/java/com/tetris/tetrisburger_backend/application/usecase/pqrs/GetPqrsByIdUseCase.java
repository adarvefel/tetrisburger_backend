package com.tetris.tetrisburger_backend.application.usecase.pqrs;

import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.GetPqrsById;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.query.GetPqrsByIdQuery;
import com.tetris.tetrisburger_backend.domain.port.out.PqrsPort;
import org.springframework.stereotype.Service;

@Service
public class GetPqrsByIdUseCase implements GetPqrsById {

    private final PqrsPort pqrsPort;

    public GetPqrsByIdUseCase(PqrsPort pqrsPort) {
        this.pqrsPort = pqrsPort;
    }

    @Override
    public Pqrs handle(GetPqrsByIdQuery getPqrsByIdQuery) {
        return pqrsPort.findById(getPqrsByIdQuery.id()).orElseThrow(()-> new RuntimeException("PQRS no encontrada con ID: " + getPqrsByIdQuery.id()));
    }
}
