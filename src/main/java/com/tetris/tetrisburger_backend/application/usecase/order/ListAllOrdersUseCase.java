package com.tetris.tetrisburger_backend.application.usecase.order;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.enums.OrderStatus;
import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.port.in.order.ListAllOrders;
import com.tetris.tetrisburger_backend.domain.port.out.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@Transactional(readOnly = true)
public class ListAllOrdersUseCase implements ListAllOrders {

    private final OrderRepository orderRepository;

    public ListAllOrdersUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public PageResponse<Order> handle(OrderStatus status, LocalDate date,
                                      PaginationRequest pagination) {
        LocalDate target = date != null ? date : LocalDate.now();

        LocalDateTime start = target.atStartOfDay();
        LocalDateTime end   = target.atTime(LocalTime.MAX);

        return orderRepository.findAll(status, start, end, pagination);
    }
}