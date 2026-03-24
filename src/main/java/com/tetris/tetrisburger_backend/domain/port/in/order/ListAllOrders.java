package com.tetris.tetrisburger_backend.domain.port.in.order;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.enums.OrderStatus;
import com.tetris.tetrisburger_backend.domain.model.Order;

import java.time.LocalDate;

public interface ListAllOrders {
    PageResponse<Order> handle(OrderStatus status, LocalDate date,
                               PaginationRequest pagination);
}