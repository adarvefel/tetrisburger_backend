package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.model.Pqrs;

public interface PqrsPort {

    Pqrs savePqrs(Pqrs pqrs);
}
