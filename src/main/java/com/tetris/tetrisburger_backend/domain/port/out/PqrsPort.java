package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.model.Pqrs;

import java.util.Optional;

public interface PqrsPort {

    Pqrs savePqrs(Pqrs pqrs);

    Optional<Pqrs> findById(Integer id);
}
