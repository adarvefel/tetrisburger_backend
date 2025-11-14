package com.tetris.tetrisburger_backend.domain.port.in.pqrs;

import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.CreatePqrsCommand;

public interface CreatePqrs {
    Pqrs handle(CreatePqrsCommand createPqrsCommand);
}
