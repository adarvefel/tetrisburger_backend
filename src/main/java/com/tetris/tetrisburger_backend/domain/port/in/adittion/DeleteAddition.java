package com.tetris.tetrisburger_backend.domain.port.in.adittion;


import com.tetris.tetrisburger_backend.domain.model.Addition;

public interface DeleteAddition {
    Addition handle(Integer id);
}
