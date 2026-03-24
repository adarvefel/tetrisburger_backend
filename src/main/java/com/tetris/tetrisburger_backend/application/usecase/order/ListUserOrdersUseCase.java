package com.tetris.tetrisburger_backend.application.usecase.order;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.port.in.order.ListUserOrders;
import com.tetris.tetrisburger_backend.domain.port.out.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListUserOrdersUseCase implements ListUserOrders {

    private final OrderRepository orderRepository;

    public ListUserOrdersUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public PageResponse<Order> handle(Integer idUser, PaginationRequest pagination) {
        return orderRepository.findByUserId(idUser, pagination);
    }
}