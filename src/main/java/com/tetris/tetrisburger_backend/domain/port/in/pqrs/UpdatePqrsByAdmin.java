package com.tetris.tetrisburger_backend.domain.port.in.pqrs;

import com.tetris.tetrisburger_backend.domain.model.Pqrs;
import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.UpdatePqrsByAdminCommand;

public interface UpdatePqrsByAdmin {
    Pqrs handle(UpdatePqrsByAdminCommand updatePqrsByAdminCommand);
}
