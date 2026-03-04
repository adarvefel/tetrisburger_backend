package com.tetris.tetrisburger_backend.domain.port.in.adittion;

import com.tetris.tetrisburger_backend.domain.model.Addition;

import java.util.Optional;

public interface GetAdditionById {
    Addition execute(Integer id);
}