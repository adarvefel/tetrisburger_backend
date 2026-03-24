package com.tetris.tetrisburger_backend.domain.port.in.order;

import com.tetris.tetrisburger_backend.domain.model.Order;

public interface CancelOrder {
    Order handle(Integer idOrder, Integer idUser);
}