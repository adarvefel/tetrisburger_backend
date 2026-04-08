package com.tetris.tetrisburger_backend.domain.port.in.pqrs;

import com.tetris.tetrisburger_backend.domain.port.in.pqrs.command.DeleteSoftPqrsCommand;

public interface DeleteSoftPqrs {
    void handle(DeleteSoftPqrsCommand deleteSoftPqrsCommand);
}
