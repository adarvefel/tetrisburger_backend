package com.tetris.tetrisburger_backend.domain.port.in.order;

import com.tetris.tetrisburger_backend.domain.enums.OrderStatus;
import com.tetris.tetrisburger_backend.domain.model.Order;

public interface UpdateOrderStatus {
    Order handle(Integer idOrder, OrderStatus newStatus, Integer employeeId);
}
