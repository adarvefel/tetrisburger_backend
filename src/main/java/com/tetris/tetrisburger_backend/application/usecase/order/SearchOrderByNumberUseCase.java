package com.tetris.tetrisburger_backend.application.usecase.order;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.port.in.order.SearchOrderByNumber;
import com.tetris.tetrisburger_backend.domain.port.out.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SearchOrderByNumberUseCase implements SearchOrderByNumber {

    private final OrderRepository orderRepository;

    public SearchOrderByNumberUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public PageResponse<Order> handle(String orderNumber, PaginationRequest pagination) {
        if (orderNumber == null || orderNumber.isBlank()) {
            return orderRepository.findAll(null, null, null, pagination);
        }
        return orderRepository.findByOrderNumber(orderNumber.trim().toUpperCase(), pagination);
    }
}