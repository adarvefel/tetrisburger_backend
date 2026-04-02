package com.tetris.tetrisburger_backend.domain.port.in.order;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Order;

public interface ListUserOrders {
    PageResponse<Order>  handle(Integer idUser, PaginationRequest pagination);
}
