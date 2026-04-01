package com.tetris.tetrisburger_backend.domain.port.in.order;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Order;

public interface SearchOrderByNumber {
    PageResponse<Order> handle(String orderNumber, PaginationRequest pagination);
}