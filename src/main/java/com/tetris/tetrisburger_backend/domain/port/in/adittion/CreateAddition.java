package com.tetris.tetrisburger_backend.domain.port.in.adittion;

import com.tetris.tetrisburger_backend.domain.model.Addition;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.command.CreateAdditionCommand;

public interface CreateAddition {
    Addition handle(CreateAdditionCommand cmd);
}
