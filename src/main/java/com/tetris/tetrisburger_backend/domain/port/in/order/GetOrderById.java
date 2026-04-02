package com.tetris.tetrisburger_backend.domain.port.in.order;

import com.tetris.tetrisburger_backend.domain.model.Order;

public interface GetOrderById {
    Order handle(Integer idOrder, Integer idUser);
}
